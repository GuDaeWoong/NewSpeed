package com.example.newspeed.user.dto;

import lombok.Getter;

@Getter
public class UpdateProfileRequestDto {
    private String nickname;
    private String userUrl;
    private String password;
}
