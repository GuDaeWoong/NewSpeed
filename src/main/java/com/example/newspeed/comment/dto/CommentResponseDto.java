package com.example.newspeed.comment.dto;

import com.example.newspeed.comment.entity.Comment;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;


import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {

    private Long commentId;
    private Long userId;
    private String userNickname;
    private String userUrl;
    private Long postId;
    private String contents;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public CommentResponseDto(
            Long id, Long userId, String nickname, String userUrl, Long postId, @NotNull String contents, LocalDateTime createdAt, LocalDateTime modifiedAt) {

        this.commentId = id;
        this.userId = userId;
        this.userNickname = nickname;
        this.userUrl = userUrl;
        this.postId = postId;
        this.contents = contents;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;

    }

    public static CommentResponseDto toDto(Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getUser().getId(),
                comment.getUser().getNickname(),
                comment.getUser().getUserUrl(),
                comment.getPost().getId(),
                comment.getContents(),
                comment.getCreatedAt(),
                comment.getModifiedAt()
        );
    }
}
