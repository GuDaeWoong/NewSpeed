package com.example.newspeed.post.dto;

import lombok.Getter;

@Getter
public class PostRequestDto {

    private final String title;
    private final String contents;
    private final String imageUrl;

    public PostRequestDto(String title, String contents, String imageUrl) {
        this.title = title;
        this.contents = contents;
        this.imageUrl = imageUrl;
    }
}
