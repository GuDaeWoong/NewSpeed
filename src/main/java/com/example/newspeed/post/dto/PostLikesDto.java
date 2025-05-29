package com.example.newspeed.post.dto;

import lombok.Getter;

@Getter
public class PostLikesDto {

    private final Long userId;
    private final Long postId;

    public PostLikesDto(Long userId, Long postId) {
        this.userId = userId;
        this.postId = postId;
    }
}
