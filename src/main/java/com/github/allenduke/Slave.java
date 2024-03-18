package com.github.allenduke;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class Slave<K, V> extends Node<K, V> {

    private Leader<K, V> leader;

    private Map<K, V> preMap = new HashMap<>();

    private Map<K, V> committedButNotComplete = new HashMap<>();

    public Slave(long id, String logPath) throws IOException {
        super(id, logPath);
    }

    public void setLeader(Leader leader) {
        this.leader = leader;
    }

    public boolean preSet(K k, V v) {
        try {
            wal(k, v);
            preMap.put(k, v);
            // 根据2pl协议，此时返回成功，后续必定可以提交成功
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    @Override
    public V get(K k) {
        /**
         * 事务在prepare和部分提交状态不可以读到最新。
         * 一种做法是发现有事务且未完成，从leader处读取数据。
         * 另一种做法是只在部分提交阶段才从leader出读取。
         */


        if(committedButNotComplete.containsKey(k)){
            // 可能在部分提交状态
        }
        return super.get(k);
    }

    public void commit(K k) {
        try {
            super.commit(k);
        } catch (IOException e) {
            // 重试直至成功
        }
        // 注意此时还是部分提交中，需要等待到全部提交后才可读到最新，避免过程中出现不一致
        committedButNotComplete.put(k, preMap.get(k));
    }

    public void complete(K k) {
        committedButNotComplete.remove(k);
        map.put(k, preMap.remove(k));
    }

    public void rollback(K k) {
        preMap.remove(k);
    }

    @Override
    public V set(K k, V v) {
        // salve不能写入，转发到leader
        return leader.set(k, v);
    }
}
