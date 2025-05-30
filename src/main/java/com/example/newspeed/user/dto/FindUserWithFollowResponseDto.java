package com.example.newspeed.user.dto;

import com.example.newspeed.user.entity.User;
import lombok.Getter;

@Getter
public class FindUserWithFollowResponseDto {

    private final Long userId;

    private final String email;

    private final String nickname;

    private final String userUrl;

    private final boolean isFollowing; // 팔로우하고 있는지 여부

    public FindUserWithFollowResponseDto(Long userId, String email, String nickname, String userUrl, boolean isFollowing) {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.userUrl = userUrl;
        this.isFollowing = isFollowing;
    }

    public static FindUserWithFollowResponseDto toDto (User user, boolean isFollowing) {
        return new FindUserWithFollowResponseDto(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getUserUrl(),
                isFollowing
        );
    }
}
