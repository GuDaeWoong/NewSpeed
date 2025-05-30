package com.example.newspeed.auth.dto;

import lombok.Getter;

@Getter
public class AccessTokenResponseDto {
    private String access_token;

    public AccessTokenResponseDto(String accessToken) {
        this.access_token = accessToken;
    }
}
