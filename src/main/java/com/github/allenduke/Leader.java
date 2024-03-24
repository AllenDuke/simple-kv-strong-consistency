package com.github.allenduke;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Leader<K, V> extends Node<K, V> {

    private List<Slave<K, V>> slaveList = new ArrayList<>();

    private Map<K, Lock> writeLockMap = new ConcurrentHashMap<>();

    private Map<K, Set<Slave<K, V>>> kCompleteSlaveNotifySetMap = new ConcurrentHashMap<>();

    private ExecutorService executorService = new ScheduledThreadPoolExecutor(1);

    public Leader(long id, String logPath) throws IOException {
        super(id, logPath);
    }

    public void addSlave(Slave slave) {
        this.slaveList.add(slave);
        slave.setLeader(this);
    }

    @Override
    public V set(K k, V v) {
        // 此时不接受k的写入，等待此次同步完成
        Lock lock = writeLockMap.computeIfAbsent(k, key -> new ReentrantLock());
        // 或者tryLock失败后返回
        try {
            lock.lock();

            for (Slave<K, V> slave : slaveList) {
                // 预提交
                if (!slave.preSet(k, v)) {
                    throw new IllegalStateException();
                }
            }
            // 提交
            for (Slave<K, V> slave : slaveList) {
                slave.commit(k);
            }

            // 全部提交完成

            v = super.set(k, v);

            for (Slave<K, V> slave : slaveList) {
                Set<Slave<K, V>> slaves = kCompleteSlaveNotifySetMap.computeIfAbsent(k, key -> new HashSet<>());
                synchronized (slaves) {
                    slaves.add(slave);
                }
            }

            // 异步通知salve 可读最新
            asyncNotifyK(k);
            return v;
            // 写入完成后，下一次从salve中读k，必定要读到最新v
        } catch (Exception e) {
            for (Slave<K, V> slave : slaveList) {
                slave.rollback(k);
            }
            throw e;
        } finally {
            lock.unlock();
        }
    }

    public V queryKFromSlaveWhileHalfCommit(Slave<K, V> slave, K k) {
        Set<Slave<K, V>> slaves = kCompleteSlaveNotifySetMap.get(k);
        if (slaves == null) {
            // 不存在半提交
            return null;
        }
        if (!slaves.contains(slave)) {
            // 当前slave已通知可读最新
            return null;
        }
        synchronized (slaves) {
            if (!slaves.contains(slave)) {
                // 当前slave已通知可读最新
                return null;
            }
            // 通知可读
            slave.complete(k);
            return slave.get(k);
        }
    }

    public void asyncNotifyK(K k) {
        executorService.submit(() -> {
            Set<Slave<K, V>> slaves = kCompleteSlaveNotifySetMap.get(k);
            // 构建一个当前视图
            Set<Slave<K, V>> view = new HashSet<>(slaves);
            for (Slave<K, V> slave : view) {
                // 可能因为slave主动询问queryKFromSlaveWhileHalfCommit得到
                if (!slaves.contains(slave)) {
                    continue;
                }
                synchronized (slaves) {
                    if (!slaves.contains(slave)) {
                        continue;
                    }
                    slave.complete(k);
                    slaves.remove(slave);
                }
            }
            kCompleteSlaveNotifySetMap.remove(k);
        });
    }
}
