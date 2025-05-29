package com.example.newspeed.comment.dto;

import lombok.Getter;

@Getter
public class DeleteCommentDto {
    private final String password;

    public DeleteCommentDto(String password) {
        this.password = password;
    }
}
