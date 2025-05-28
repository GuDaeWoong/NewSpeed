package com.example.newspeed.post.dto;

import lombok.Getter;


/**
 * Post의 title을 포함한 데이터를 제공하는 DTO
 */
@Getter
public class PostTitleOfUserDto {

    private final String title;

    public PostTitleOfUserDto(String title) {
        this.title = title;
    }
}
