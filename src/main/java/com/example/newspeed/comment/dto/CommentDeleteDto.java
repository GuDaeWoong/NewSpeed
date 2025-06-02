package com.example.newspeed.comment.dto;

import lombok.Getter;

@Getter
public class CommentDeleteDto {
    private final String password;

    public CommentDeleteDto(String password) {
        this.password = password;
    }
}
