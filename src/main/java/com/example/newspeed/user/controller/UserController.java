package com.example.newspeed.user.controller;

import com.example.newspeed.user.dto.CreateUserRequestDto;
import com.example.newspeed.user.dto.CreateUserResponseDto;
import com.example.newspeed.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 유저 생성 (회원가입)
     * @param requestDto 이메일, 닉네임, 패스워드
     * @return 생성된 유저 정보
     */
    @PostMapping("/signup")
    public ResponseEntity<CreateUserResponseDto> createUser(@RequestBody CreateUserRequestDto requestDto) {

        CreateUserResponseDto responseDto = userService.createUser(requestDto.getEmail(),
                                                                   requestDto.getUsername(),
                                                                   requestDto.getPassword());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
}