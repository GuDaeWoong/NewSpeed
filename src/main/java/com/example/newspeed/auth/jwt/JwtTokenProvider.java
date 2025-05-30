package com.example.newspeed.auth.jwt;

import com.example.newspeed.auth.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final JwtTokenService jwtTokenService;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final TokenBlacklistService tokenBlacklistService;

    //토큰 유지 시간 (1시간)
    private final long tokenValidityInMilliseconds = 1000 * 60 * 60;

    //Access Token : 로그인 후 API 호출에 사용
    public String createAccessToken(Long userId) {
        return jwtTokenService.createToken(userId, tokenValidityInMilliseconds/4); // 15분
    }

    //Refresh Token : Access Token 이 만료됐을 때 재발급 요청에 사용
    public String createRefreshToken(Long userId) {
        return jwtTokenService.createToken(userId, tokenValidityInMilliseconds * 168); // 일주일
    }

    //Authentication 생성
    public Authentication getAuthentication(String token) {
        return jwtAuthenticationProvider.getAuthentication(token);
    }
    
    //로그인 상태 확인
    public boolean isLoggedIn(String accessToken) {
        // null 체크 및 블랙 리스트 확인
        if(accessToken == null) return false;
        return !tokenBlacklistService.isTokenInBlackList(accessToken);
    }


}
