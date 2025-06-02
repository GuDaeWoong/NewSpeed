package com.example.newspeed.post.dto;

import lombok.Getter;

@Getter
public class PostDeleteRequestDto {

    private final String password;

    public PostDeleteRequestDto(String password) {
        this.password = password;
    }
}
