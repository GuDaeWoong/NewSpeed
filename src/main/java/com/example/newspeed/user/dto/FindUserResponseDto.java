package com.example.newspeed.user.dto;

import com.example.newspeed.post.dto.PostTitleOfUserDto;
import com.example.newspeed.post.entity.Post;
import com.example.newspeed.user.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class FindUserResponseDto {

    private final Long userId;

    private final String email;

    private final String nickname;

    private String userUrl;

    // 팔로우 수
    private final Long followCount;

    private final LocalDateTime createdAt;

    private final LocalDateTime modifiedAt;

    private final List<PostTitleOfUserDto> posts;

    public FindUserResponseDto(
            Long userId, String email, String nickname, String userUrl,
            Long followCount,
            LocalDateTime createdAt, LocalDateTime modifiedAt, List<PostTitleOfUserDto> posts)
    {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.userUrl = userUrl;
        this.followCount = followCount;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.posts = posts;
    }

    public static FindUserResponseDto toDto (User user, long followCount) {
        return new FindUserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getUserUrl(),
                followCount, // 팔로우 수 (User 가 팔로우한 유저 목록의 크기)
                user.getCreatedAt(),
                user.getModifiedAt(),
                user.getPosts().stream() // 유저의 Post 리스트
                        .map(Post::toDto) // 각 Post 를 DTO 로 변환
                        .toList() // Post 리스트를 DTO 리스트로 변환
        );
    }
}
