package com.example.newspeed.post.dto;

import com.example.newspeed.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FindPostResponseDto {

    private final Long id;
    private final String nickname;
    private final String title;
    private final String contents;
    private final String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public FindPostResponseDto(Long id, String nickname, String title, String contents, String imageUrl, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.nickname = nickname;
        this.title = title;
        this.contents = contents;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static FindPostResponseDto toPostDto(Post post) {
        return new FindPostResponseDto(
                post.getId(),
                "닉네임",  //post.getUser().getNickname(),
                post.getTitle(),
                post.getContents(),
                post.getImageUrl(),
                post.getCreatedAt(),
                post.getModifiedAt()
        );
    }
}
