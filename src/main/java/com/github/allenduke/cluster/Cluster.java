/*
 * Copyright (c) allenduke 2024.
 */

package com.github.allenduke.cluster;

import com.github.allenduke.Node;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author allenduke
 * @description
 * @contact AllenDuke@163.com
 * @date 2024/5/5
 */
@Component
public class Cluster {

    @Value("${node.others}")
    private String others;

    private Map<Integer, RemoteNode> allMap = new HashMap<>();

    private Map<Integer, RemoteNode> connectedMap = new HashMap<>();

    private Map<Integer, RemoteNode> disconnectedMap = new HashMap<>();

    @Resource
    private Node node;

    @PostConstruct
    public void init() {
        String[] split = others.split(",");
        for (int i = 0; i < split.length; ) {
            addRemoteNode(Integer.parseInt(split[i]), split[i + 1], Integer.parseInt(split[i + 2]));
        }
    }

    public synchronized void addRemoteNode(int id, String ip, int port) {
        RemoteNode remoteNode = new RemoteNode();
        remoteNode.setId(id);
        remoteNode.setIp(ip);
        remoteNode.setPort(port);

        allMap.put(id, remoteNode);
    }

    public void online(int id) {
        disconnectedMap.remove(id);
        connectedMap.put(id, allMap.get(id));
    }

    public void offline(int id) {
        connectedMap.remove(id);
        disconnectedMap.put(id, allMap.get(id));
    }

    public Map<Integer, RemoteNode> getAllMap() {
        return allMap;
    }

    public Map<Integer, RemoteNode> getConnectedMap() {
        return connectedMap;
    }

    public Map<Integer, RemoteNode> getDisconnectedMap() {
        return disconnectedMap;
    }
}
