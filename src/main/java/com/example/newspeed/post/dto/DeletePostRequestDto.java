package com.example.newspeed.post.dto;

import lombok.Getter;

@Getter
public class DeletePostRequestDto {

    private final String password;

    public DeletePostRequestDto(String password) {
        this.password = password;
    }
}
