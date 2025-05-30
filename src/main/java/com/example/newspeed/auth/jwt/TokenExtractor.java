package com.example.newspeed.auth.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TokenExtractor {

    private final JwtTokenService jwtTokenService;

    // Header 에서 access token 값을 가져온다.
    public Optional<String> extractAccessTokenFromHeader(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization")) // Authorization 헤더의 값을 Optional로 감싼다.
                .filter(header -> header.startsWith("Bearer ")) // 값이 "Bearer "로 시작하는지 확인 - Bearer : 토큰 종류
                .map(header -> header.substring(7)); // "Bearer " 길이만큼 잘라서 리턴
    }

    // Cookie 에서 refresh token 값을 가져온다.
    public Optional<String> extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return Optional.empty();

        //쿠키에서 토큰 찾아서 유효성 검증 후 리턴
        return getRefreshTokenFromCookie(cookies);
    }

    //쿠키에서 refresh_token 이름으로 토큰 받기
    private Optional<String> getRefreshTokenFromCookie(Cookie[] cookies){
        for (Cookie cookie : cookies) {
            if ("refresh_token".equals(cookie.getName())) {
                String refreshToken = cookie.getValue();
                if(jwtTokenService.validateToken(refreshToken)){
                    return Optional.ofNullable(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }

}
