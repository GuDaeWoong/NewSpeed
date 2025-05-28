package com.example.newspeed.user.dto;

import lombok.Getter;

@Getter
public class followDto {

    private final Long userId;
    private final Long followId;

    public followDto(Long userId, Long followId) {
        this.userId = userId;
        this.followId = followId;
    }
}
