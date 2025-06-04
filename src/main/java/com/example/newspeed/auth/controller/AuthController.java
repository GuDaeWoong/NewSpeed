package com.example.newspeed.auth.controller;

import com.example.newspeed.auth.dto.AccessTokenDto;
import com.example.newspeed.auth.dto.TokenDto;
import com.example.newspeed.auth.jwt.*;
import com.example.newspeed.auth.dto.LoginRequestDto;
import com.example.newspeed.auth.service.AuthService;
import com.example.newspeed.global.Enums.ErrorCode;
import com.example.newspeed.global.error.CustomException;
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
    private final TokenExtractor tokenExtractor;

    @PostMapping("/login")
    public ResponseEntity<AccessTokenDto> login(@Valid @RequestBody LoginRequestDto requestDto,
                                                HttpServletResponse response){

        //비밀번호 확인 후 토큰 변환 후 반환
        TokenDto token = authService.login(requestDto);

        // refresh 토큰을 쿠키에 추가
        tokenCookieUtils.addRefreshTokenToCookie(token.getRefreshToken(), response);

        return new ResponseEntity<>(new AccessTokenDto(token.getAccessToken()),HttpStatus.OK);

    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request,
                                       HttpServletResponse response){

        //Header 에서 Token 가져오기
        String accessToken = tokenExtractor.extractAccessTokenFromHeader(request).orElse(null);
        String refreshToken = tokenExtractor.extractRefreshTokenFromCookie(request).orElse(null);

        authService.logout(new TokenDto(accessToken,refreshToken));

        //쿠키에서 refreshToken 리셋
        tokenCookieUtils.deleteRefreshTokenCookie(response);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/reissue")
    public ResponseEntity<AccessTokenDto> reissue(HttpServletRequest request,
                                                  HttpServletResponse response) {

        //쿠키에서 refresh token 꺼내기
        String refreshToken = tokenExtractor.extractRefreshTokenFromCookie(request)
                .orElseThrow(()-> new CustomException(ErrorCode.REQUIRED_LOGIN));


        AccessTokenDto newAccessToken = authService.reissueAccessToken(refreshToken);

        //DB 에서 refresh 토큰 불일치시 비정상적인 접근으로 판단 refresh 토큰 쿠키에서 리셋
        if(newAccessToken == null) {
            tokenCookieUtils.deleteRefreshTokenCookie(response);
            throw new CustomException(ErrorCode.INVALID_ACCESS);
        }

        return new ResponseEntity<>(newAccessToken, HttpStatus.OK);
    }

}
