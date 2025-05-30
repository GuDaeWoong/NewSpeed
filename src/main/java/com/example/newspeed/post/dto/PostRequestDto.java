package com.example.newspeed.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PostRequestDto {

    @NotBlank
    private final String title;

    @NotBlank
    private final String contents;

    @NotBlank
    private final String imageUrl;

    public PostRequestDto(String title, String contents, String imageUrl) {
        this.title = title;
        this.contents = contents;
        this.imageUrl = imageUrl;
    }
}
