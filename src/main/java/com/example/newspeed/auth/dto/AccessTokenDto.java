package com.example.newspeed.auth.dto;

import lombok.Getter;

@Getter
public class AccessTokenDto {
    private String access_token;

    public AccessTokenDto(String accessToken) {
        this.access_token = accessToken;
    }
}
