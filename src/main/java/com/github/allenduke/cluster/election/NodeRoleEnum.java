/*
 * Copyright (c) allenduke 2024.
 */

package com.github.allenduke.cluster.election;

/**
 * @author allenduke
 * @description 节点角色枚举
 * @contact AllenDuke@163.com
 * @date 2024/5/5
 */
public enum NodeRoleEnum {

    LEADER(1),

    CANDIDATE(2),

    SLAVE(3);

    private int value;

    NodeRoleEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
