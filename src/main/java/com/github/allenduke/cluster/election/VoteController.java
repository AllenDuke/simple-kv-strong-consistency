/*
 * Copyright (c) allenduke 2024.
 */

package com.github.allenduke.cluster.election;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author allenduke
 * @description 接受投票
 * @contact AllenDuke@163.com
 * @date 2024/5/5
 */

@RestController
public class VoteController {

    @PostMapping("/inside/vote")
    public VoteResponse vote(VoteRequest voteRequest) {
        return null;
    }
}
