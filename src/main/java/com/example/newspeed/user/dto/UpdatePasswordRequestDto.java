package com.example.newspeed.user.dto;

import lombok.Getter;

@Getter
public class UpdatePasswordRequestDto {
    private String oldPassword;
    private String newPassword;
}
