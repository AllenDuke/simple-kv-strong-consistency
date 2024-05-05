/*
 * Copyright (c) allenduke 2024.
 */

package com.github.allenduke.cluster;

import com.github.allenduke.cluster.election.NodeRoleEnum;

/**
 * @author allenduke
 * @description
 * @contact AllenDuke@163.com
 * @date 2024/5/5
 */
public class RemoteNode {

    private int id;
    private String ip;
    private int port;
    private NodeRoleEnum role;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public NodeRoleEnum getRole() {
        return role;
    }

    public void setRole(NodeRoleEnum role) {
        this.role = role;
    }

    public String toString() {
        return "RemoteNode{" +
                "id=" + id +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", role=" + role +
                '}';
    }
}
