package com.example.newspeed.comment.dto;

import com.example.newspeed.comment.entity.Comment;
import lombok.Getter;


import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {

    private Long commentId;
    private Long userId;
    private Long postId;
    private String contents;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public CommentResponseDto(
            Long commentId, Long userId, Long postId, String contents, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.commentId = commentId;
        this.userId = userId;
        this.postId = postId;
        this.contents = contents;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }


    public static CommentResponseDto toDto(Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getUser().getId(),
                comment.getPost().getId(),
                comment.getContents(),
                comment.getCreatedAt(),
                comment.getModifiedAt()
        );
    }
}
