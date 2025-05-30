package com.example.newspeed.post.dto;

import com.example.newspeed.post.entity.Post;
import com.example.newspeed.post.entity.PostLikes;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PostResponseDto {

    private final Long id;
    private final Long userId;
    private final String title;
    private final String contents;
    private final String imageUrl;
    private final String userUrl;
    private final int postLikes;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public PostResponseDto(Long id,
                           Long userId,
                           String title,
                           String contents,
                           String imageUrl,
                           String userUrl,
                           int postLikes,
                           LocalDateTime createdAt,
                           LocalDateTime modifiedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.contents = contents;
        this.imageUrl = imageUrl;
        this.userUrl = userUrl;
        this.postLikes = postLikes;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
