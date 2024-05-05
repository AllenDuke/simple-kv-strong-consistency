/*
 * Copyright (c) allenduke 2024.
 */

package com.github.allenduke;

import com.github.allenduke.cluster.election.NodeRoleEnum;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class Node {

    @Value("${node.id}")
    private Integer id;

    @Value("${kv.data.dir}")
    private String dataDir;

    private BufferedWriter bufferedWriter;

    private BufferedReader bufferedReader;

    private NodeRoleEnum nodeRoleEnum;

    private AtomicLong lastInstructionId = new AtomicLong(0);

    private LoadingCache<String, ReadWriteLock> lockCache = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build(new CacheLoader<String, ReadWriteLock>() {
                @Override
                public ReadWriteLock load(String key) throws Exception {
                    return new ReentrantReadWriteLock();
                }
            });

    @PostConstruct
    public void init() throws IOException {
        // 追加写
        bufferedWriter = new BufferedWriter(new FileWriter(dataDir + "/" + id + ".data", true));
        bufferedReader = new BufferedReader(new FileReader(dataDir + "/" + id + ".data"));
        recover();

        // 节点
        nodeRoleEnum = NodeRoleEnum.CANDIDATE;
    }

    private void recover() throws IOException {
        bufferedReader.lines().forEach(line -> {
            String[] split = line.split(" ");
            lastInstructionId.set(Long.parseLong(split[0]));
            map.put(split[1], split[2]);
        });
    }

    protected final Map<String, String> map = new ConcurrentHashMap();

    public String get(String k) throws ExecutionException {
        checkElected();
        if (nodeRoleEnum == NodeRoleEnum.SLAVE) {
            // 转移请求给master 或者 锁定后从slave查询

        }
        ReadWriteLock lock = lockCache.get(k);
        if (!lock.readLock().tryLock()) {
            throw new IllegalStateException("冲突，稍后再试！");
        }
        try {
            return map.get(k);
        } finally {
            lock.readLock().unlock();
        }
    }

    public String setFromMaster(long instructionId, String k, String v) {
        if (nodeRoleEnum == NodeRoleEnum.SLAVE) {

        }
        return map.put(k, v);
    }

    public synchronized void checkElected() {
        if (nodeRoleEnum != NodeRoleEnum.CANDIDATE) {
            return;
        }
        // todo 开始选举
    }

    public String setAsMaster(String k, String v) throws ExecutionException {
        checkElected();
        if (nodeRoleEnum == NodeRoleEnum.SLAVE) {
            // 转移请求给master
        }

        ReadWriteLock lock = lockCache.get(k);
        if (!lock.writeLock().tryLock()) {
            throw new IllegalStateException("冲突，稍后再试！");
        }
        try {
            lastInstructionId.incrementAndGet();
            try {
                wal(lastInstructionId.get(), k, v);
                // 通知slave
            } catch (IOException e) {
                throw new IllegalStateException("数据写入失败");
            }
            return map.put(k, v);
        } finally {
            lock.writeLock().unlock();
        }
    }

    protected void wal(Long instructionId, String k, String v) throws IOException {
        bufferedWriter.write(instructionId.toString());
        bufferedWriter.write(k);
        bufferedWriter.write(v);

        // 刷到磁盘
        bufferedWriter.flush();
    }
}
