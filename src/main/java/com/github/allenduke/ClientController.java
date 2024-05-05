/*
 * Copyright (c) allenduke 2024.
 */

package com.github.allenduke;

import com.github.allenduke.cluster.Cluster;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

/**
 * @author allenduke
 * @description 接受客户端请求
 * @contact AllenDuke@163.com
 * @date 2024/5/5
 */
@RestController
public class ClientController {

    @Resource
    private Node node;

    @GetMapping("/get")
    public String get(@RequestParam("key") String key) throws ExecutionException {
        return node.get(key);
    }

    @PostMapping("/set")
    public String set(@RequestParam("key") String key, @RequestParam("value") String value) throws ExecutionException {
        return node.setAsMaster(key, value);
    }
}
