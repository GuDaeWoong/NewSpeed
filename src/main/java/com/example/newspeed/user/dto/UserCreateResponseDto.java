package com.example.newspeed.user.dto;

import java.time.LocalDateTime;

import com.example.newspeed.user.entity.User;

import lombok.Getter;

@Getter
public class UserCreateResponseDto {
    private Long userId;
    private String email;
    private String nickname;
    private String userUrl;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static UserCreateResponseDto toDto(User user) {
        final UserCreateResponseDto response = new UserCreateResponseDto();

        response.userId = user.getId();
        response.email = user.getEmail();
        response.nickname = user.getNickname();
        response.userUrl = user.getUserUrl();
        response.createdAt = user.getCreatedAt();
        response.modifiedAt = user.getModifiedAt();

        return response;
    }
}
