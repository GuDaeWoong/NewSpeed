package com.example.newspeed.user.dto;

import lombok.Getter;

@Getter
public class CreateUserRequestDto {
    private String email;
    private String nickname;
    private String userUrl;
    private String password;
}
