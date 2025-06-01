package com.example.newspeed.comment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentUpdateResponseDto {

    private String contents;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public CommentUpdateResponseDto(@NotNull String contents, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.contents = contents;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
