package com.example.newspeed.post.dto;

import com.example.newspeed.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FindAllPostResponseDto {

    private final Long id;
    private final String nickname;
    private final String title;
    private final String contents;
    private final String imageUrl;
    private final String userUrl;
    private final int postLikes;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public FindAllPostResponseDto(Long id,
                                  String nickname,
                                  String title,
                                  String contents,
                                  String imageUrl,
                                  String userUrl,
                                  int postLikes,
                                  LocalDateTime createdAt,
                                  LocalDateTime modifiedAt
    ) {
        this.id = id;
        this.nickname = nickname;
        this.title = title;
        this.contents = contents;
        this.imageUrl = imageUrl;
        this.userUrl = userUrl;
        this.postLikes = postLikes;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static FindAllPostResponseDto toPostDto(Post post) {
        return new FindAllPostResponseDto(
                post.getId(),
                post.getUser().getNickname(),
                post.getTitle(),
                post.getContents(),
                post.getImageUrl(),
                post.getUserUrl(),
                post.getPostLikes().size(),
                post.getCreatedAt(),
                post.getModifiedAt()
        );
    }
}
