package com.example.newspeed.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommentDeleteDto {

    @NotBlank
    private final String password;

    public CommentDeleteDto(String password) {
        this.password = password;
    }
}
