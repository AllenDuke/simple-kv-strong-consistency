package com.github.allenduke;

import java.io.*;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Node<K, V> {

    private long id;

    private String logPath;

    private BufferedWriter bufferedWriter;

    public Node(long id, String logPath) throws IOException {
        this.id = id;
        this.logPath = logPath;
        // 追加写
        bufferedWriter = new BufferedWriter(new FileWriter(logPath, true));
    }

    protected final Map<K, V> map = new ConcurrentHashMap<K, V>();

    public V get(K k) {
        return map.get(k);
    }

    public V set(K k, V v) {
        return map.put(k, v);
    }

    protected void wal(K k, V v) throws IOException {
        bufferedWriter.write(k.toString());
        bufferedWriter.write(v.toString());

        // 刷到磁盘
        bufferedWriter.flush();
    }

    protected void commit(K k) throws IOException {
        bufferedWriter.write(k.toString());

        // 刷到磁盘
        bufferedWriter.flush();
    }
}
