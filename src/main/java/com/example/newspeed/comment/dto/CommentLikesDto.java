package com.example.newspeed.comment.dto;

import lombok.Getter;

@Getter
public class CommentLikesDto {

    private final Long userId;
    private final Long commentId;

    public CommentLikesDto(Long userId, Long commentId) {
        this.userId = userId;
        this.commentId = commentId;
    }
}
