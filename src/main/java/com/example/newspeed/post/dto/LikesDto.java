package com.example.newspeed.post.dto;

import lombok.Getter;

@Getter
public class LikesDto {

    private final Long userId;
    private final Long postId;

    public LikesDto(Long userId, Long postId) {
        this.userId = userId;
        this.postId = postId;
    }
}
