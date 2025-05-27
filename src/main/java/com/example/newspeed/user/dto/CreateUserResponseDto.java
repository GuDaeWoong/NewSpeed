package com.example.newspeed.user.dto;

import com.example.newspeed.user.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CreateUserResponseDto {
    private Long id;
    private String username;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static CreateUserResponseDto toDto(User user) {
        final CreateUserResponseDto response = new CreateUserResponseDto();

        response.id = user.getId();
        response.username = user.getUsername();
        response.email = user.getEmail();
        response.createdAt = user.getCreatedAt();
        response.modifiedAt = user.getModifiedAt();

        return response;
    }
}
