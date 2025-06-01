package com.example.newspeed.comment.dto;

import com.example.newspeed.comment.entity.Comment;
import com.example.newspeed.post.dto.FindAllPostResponseDto;
import com.example.newspeed.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentWithLikesDto {
    private Long commentId;
    private Long userId;
    private String userNickname;
    private String userUrl;
    private Long postId;
    private String contents;
    private Long likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public CommentWithLikesDto(
            Long commentId,
            Long userId,
            String userNickname,
            String userUrl,
            Long postId,
            String contents,
            Long likeCount, // 좋아요 개수
            LocalDateTime createdAt,
            LocalDateTime modifiedAt) {

        this.commentId = commentId;
        this.userId = userId;
        this.userNickname = userNickname;
        this.userUrl = userUrl;
        this.postId = postId;
        this.contents = contents;
        this.likeCount = likeCount;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

}
