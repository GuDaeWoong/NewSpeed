package com.example.newspeed.user.dto;

import com.example.newspeed.user.entity.User;
import lombok.Getter;

@Getter
public class UpdateProfileResponseDto {
    private Long userId;
    private String nickname;
    private String userUrl;

    public UpdateProfileResponseDto(Long userId, String nickname, String userUrl) {
        this.userId = userId;
        this.nickname = nickname;
        this.userUrl = userUrl;
    }

    public static UpdateProfileResponseDto toDto(User user) {
        return new UpdateProfileResponseDto(user.getId(),
                                            user.getNickname(),
                                            user.getUserUrl());
    }
}
