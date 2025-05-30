package com.example.newspeed.auth.controller;

import com.example.newspeed.auth.jwt.*;
import com.example.newspeed.auth.dto.AccessTokenResponseDto;
import com.example.newspeed.user.dto.LoginRequestDto;
import com.example.newspeed.auth.dto.TokenResponseDto;
import com.example.newspeed.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TokenCookieUtils tokenCookieUtils;

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto,
                                                        HttpServletResponse response){

        //비밀번호 확인 후 토큰 변환 후 반환
        TokenResponseDto token = authService.login(requestDto);

        // refresh 토큰을 쿠키에 추가
        tokenCookieUtils.addRefreshTokenToCookie(token.getRefreshToken(), response);

        return new ResponseEntity<>(new AccessTokenResponseDto(token.getAccessToken()),HttpStatus.OK);

    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request,
                                       HttpServletResponse response){

        authService.logout(request, response);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/reissue")
    public ResponseEntity<AccessTokenResponseDto> reissue(HttpServletRequest request) {

        String newAccessToken = authService.reissueAccessToken(request);

        return new ResponseEntity<>(new AccessTokenResponseDto(newAccessToken), HttpStatus.OK);
    }

}
