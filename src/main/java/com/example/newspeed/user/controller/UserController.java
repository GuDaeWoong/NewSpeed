package com.example.newspeed.user.controller;

import com.example.newspeed.global.common.JwtTokenProvider;
import com.example.newspeed.user.dto.*;
import com.example.newspeed.user.dto.CreateUserRequestDto;
import com.example.newspeed.user.dto.CreateUserResponseDto;
import com.example.newspeed.user.dto.FindUserResponseDto;

import com.example.newspeed.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    private final JwtTokenProvider jwtTokenProvider;

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

    @GetMapping("/{userId}")
    public ResponseEntity<FindUserResponseDto> findByIdUser(@PathVariable Long userId) {

        FindUserResponseDto findUserResponseDto = userService.findByIdUser(userId);

        return new ResponseEntity<>(findUserResponseDto, HttpStatus.OK);
    }

    @PatchMapping("/profile")
    public ResponseEntity<UpdateProfileResponseDto> updateProfile(@RequestBody UpdateProfileRequestDto requestDto) {

        // JwtTokenProvider를 통해 로그인 유저 ID 가져오기
        Long currentUserId = jwtTokenProvider.getUserIdFromSecurity();

        UpdateProfileResponseDto responseDto = userService.updateProfile(currentUserId,
                                                                         requestDto.getNickname(),
                                                                         requestDto.getUserUrl(),
                                                                         requestDto.getPassword());
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
    /**
     * 로그인 유저 정보 조회 (마이페이지 조회)
     * @return 정상 조회 시 JWT 토큰으로 확인한 로그인 유저의 유저 정보 + 200 OK 반환
     */
    @GetMapping("/me")
    public ResponseEntity<FindUserResponseDto> findMyPage() {
        Long currentUserId = jwtTokenProvider.getUserIdFromSecurity();

        FindUserResponseDto findUserResponseDto = userService.findByIdUser(currentUserId);

        return new ResponseEntity<>(findUserResponseDto, HttpStatus.OK);
    }


}