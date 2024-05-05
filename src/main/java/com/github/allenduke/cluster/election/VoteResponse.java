/*
 * Copyright (c) allenduke 2024.
 */

package com.github.allenduke.cluster.election;

import java.io.Serializable;

/**
 * @author allenduke
 * @description
 * @contact AllenDuke@163.com
 * @date 2024/5/5
 */
public class VoteResponse implements Serializable {

    private VoteRequest voteRequest;

    /**
     * 已选择的节点id
     */
    private int votedNodeId;
}
