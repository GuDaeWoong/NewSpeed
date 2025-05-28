package com.example.newspeed.user.controller;

import com.example.newspeed.user.dto.CreateUserRequestDto;
import com.example.newspeed.user.dto.CreateUserResponseDto;
import com.example.newspeed.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 유저 생성 (회원가입)
     *
     * @param requestDto 이메일, 닉네임, 프로필이미지url, 패스워드
     * @return 생성된 유저 정보
     */
    @PostMapping("/signup")
    public ResponseEntity<CreateUserResponseDto> createUser(@Valid @RequestBody CreateUserRequestDto requestDto) {

        CreateUserResponseDto responseDto = userService.createUser(requestDto.getEmail(),
                                                                   requestDto.getNickname(),
                                                                   requestDto.getUserUrl(),
                                                                   requestDto.getPassword());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
}