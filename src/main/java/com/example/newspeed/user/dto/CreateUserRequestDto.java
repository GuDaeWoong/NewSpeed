package com.example.newspeed.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class CreateUserRequestDto {
    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
             message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank
    private String nickname;

    @NotBlank
    private String userUrl;

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
             message = "영문 대소문자, 숫자, 특수문자를 각각 최소 1자 이상 포함해야 하며, 8자 이상이어야 합니다.")
    private String password;
}
