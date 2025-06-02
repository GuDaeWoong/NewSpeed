package com.example.newspeed.user.dto;

import com.example.newspeed.user.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserCreateResponseDto {
    private Long id;
    private String nickname;
    private String email;
    private String userUrl;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static UserCreateResponseDto toDto(User user) {
        final UserCreateResponseDto response = new UserCreateResponseDto();

        response.id = user.getId();
        response.nickname = user.getNickname();
        response.email = user.getEmail();
        response.userUrl = user.getUserUrl();
        response.createdAt = user.getCreatedAt();
        response.modifiedAt = user.getModifiedAt();

        return response;
    }
}
