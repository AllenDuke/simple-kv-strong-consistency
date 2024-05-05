/*
 * Copyright (c) allenduke 2024.
 */

package com.github.allenduke.cluster.election;

import java.io.Serializable;

/**
 * @author allenduke
 * @description 投票请求
 * @contact AllenDuke@163.com
 * @date 2024/5/5
 */
public class VoteRequest implements Serializable {

    /**
     * 节点id
     */
    private int nodeId;

    private long voteId;

    /**
     * 节点最大指令号
     */
    private long maxInstructionNum;
}
