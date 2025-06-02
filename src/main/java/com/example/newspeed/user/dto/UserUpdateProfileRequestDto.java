package com.example.newspeed.user.dto;

import lombok.Getter;

@Getter
public class UserUpdateProfileRequestDto {
    private String nickname;
    private String userUrl;
    private String password;
}
