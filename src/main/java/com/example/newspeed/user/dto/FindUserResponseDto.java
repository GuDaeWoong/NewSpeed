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

    // 팔로우 수
    private final Long followCount;

    private final LocalDateTime createdAt;

    private final LocalDateTime modifiedAt;

    public FindUserResponseDto(
            Long userId, String email, String nickname, String userUrl,
            Long followCount,
            LocalDateTime createdAt, LocalDateTime modifiedAt)
    {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.userUrl = userUrl;
        this.followCount = followCount;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static FindUserResponseDto toDto (User user, long followCount) {
        return new FindUserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getUserUrl(),
                followCount, // 팔로우 수 (User 가 팔로우한 유저 목록의 크기)
                user.getCreatedAt(),
                user.getModifiedAt()
        );
    }
}
