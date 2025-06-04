package com.example.newspeed.user.dto;

import lombok.Getter;

@Getter
public class FollowRequestDto {

    private final Long userId;
    private final Long followId;

    public FollowRequestDto(Long userId, Long followId) {
        this.userId = userId;
        this.followId = followId;
    }
}
