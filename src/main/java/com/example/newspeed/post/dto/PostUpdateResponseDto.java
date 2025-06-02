package com.example.newspeed.post.dto;

import com.example.newspeed.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostUpdateResponseDto {

    private final Long postId;
    private final Long userId;
    private final String nickname;
    private final String title;
    private final String contents;
    private final String imageUrl;
    private final String userUrl;
    private final int postLikes;
    private final int postComments;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public PostUpdateResponseDto(Long postId,
                                 Long userId,
                                 String nickname,
                                 String title,
                                 String contents,
                                 String imageUrl,
                                 String userUrl,
                                 int postLikes,
                                 int postComments,
                                 LocalDateTime createdAt,
                                 LocalDateTime modifiedAt
    ) {
        this.postId = postId;
        this.userId = userId;
        this.nickname = nickname;
        this.title = title;
        this.contents = contents;
        this.imageUrl = imageUrl;
        this.userUrl = userUrl;
        this.postLikes = postLikes;
        this.postComments = postComments;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static PostUpdateResponseDto toDto(Post post){
        return new PostUpdateResponseDto(
                post.getPostId(),
                post.getUser().getId(),
                post.getUser().getNickname(),
                post.getTitle(),
                post.getContents(),
                post.getImageUrl(),
                post.getUserUrl(),
                post.getPostLikes().size(),
                post.getComments().size(),
                post.getCreatedAt(),
                post.getModifiedAt()
        );
    }
}
