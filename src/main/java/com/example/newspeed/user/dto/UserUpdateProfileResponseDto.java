package com.example.newspeed.user.dto;

import com.example.newspeed.user.entity.User;
import lombok.Getter;

@Getter
public class UserUpdateProfileResponseDto {
    private Long userId;
    private String nickname;
    private String userUrl;

    public UserUpdateProfileResponseDto(Long userId, String nickname, String userUrl) {
        this.userId = userId;
        this.nickname = nickname;
        this.userUrl = userUrl;
    }

    public static UserUpdateProfileResponseDto toDto(User user) {
        return new UserUpdateProfileResponseDto(user.getId(),
                                            user.getNickname(),
                                            user.getUserUrl());
    }
}
