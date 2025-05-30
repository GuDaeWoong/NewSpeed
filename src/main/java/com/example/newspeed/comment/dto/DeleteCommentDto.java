package com.example.newspeed.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class DeleteCommentDto {
    @NotBlank
    private final String password;

    public DeleteCommentDto(String password) {
        this.password = password;
    }
}
