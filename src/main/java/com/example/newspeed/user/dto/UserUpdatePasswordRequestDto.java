package com.example.newspeed.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UserUpdatePasswordRequestDto {
    @NotBlank(message = "현재 비밀번호는 비워둘 수 없습니다.")
    private String currentPassword;

    @NotBlank(message = "새로운 비밀번호는 비워둘 수 없습니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "영문 대소문자, 숫자, 특수문자를 각각 최소 1자 이상 포함해야 하며, 8자 이상이어야 합니다.")
    private String newPassword;
}
