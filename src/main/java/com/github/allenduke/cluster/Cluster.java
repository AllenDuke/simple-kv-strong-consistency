/*
 * Copyright (c) allenduke 2024.
 */

package com.github.allenduke.cluster;

import org.springframework.stereotype.Component;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @author allenduke
 * @description
 * @contact AllenDuke@163.com
 * @date 2024/5/5
 */
@Component
public class Cluster {

    private List<SocketAddress> addresses = new ArrayList<>();

    public void add(SocketAddress addr) {
        addresses.add(addr);
    }

}
