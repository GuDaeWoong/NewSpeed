package com.example.newspeed.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class DeleteUserRequestDto {
    @NotBlank(message = "비밀번호는 비워둘 수 없습니다.")
    private String password;
}