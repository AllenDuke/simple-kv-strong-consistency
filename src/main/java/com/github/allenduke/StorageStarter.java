/*
 * Copyright (c) allenduke 2024.
 */

package com.github.allenduke;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author allenduke
 * @description
 * @contact AllenDuke@163.com
 * @date 2024/5/5
 */
@SpringBootApplication
public class StorageStarter {

    public static void main(String[] args) {
        System.setProperty("node.id", args[0]);
        // 系统内部通信端口
        System.setProperty("node.port", args[1]);

        // 客户端通信端口
        System.setProperty("server.port", args[2]);

        String others = "";
        for (int i = 3; i < args.length; i++) {
            others += args[i] + ",";
        }
        System.setProperty("node.others", others);

        SpringApplication.run(StorageStarter.class, args);
    }
}
