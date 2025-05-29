package com.example.newspeed.user.dto;

import com.example.newspeed.user.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FindUserResponseDto {

    private final Long userId;

    private final String email;

    private final String nickname;

    private final String userUrl;

    private final Long followCount; // 팔로우 수

    private final Long followerCount; // 팔로워 수

    private final LocalDateTime createdAt;

    private final LocalDateTime modifiedAt;

    public FindUserResponseDto(
            Long userId, String email, String nickname, String userUrl,
            Long followCount, Long followerCount,
            LocalDateTime createdAt, LocalDateTime modifiedAt)
    {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.userUrl = userUrl;
        this.followCount = followCount;
        this.followerCount = followerCount;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static FindUserResponseDto toDto (User user, long followCount, long followerCount) {
        return new FindUserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getUserUrl(),
                followCount,
                followerCount,// 팔로우 수 (User 가 팔로우한 유저 목록의 크기)
                user.getCreatedAt(),
                user.getModifiedAt()
        );
    }
}
