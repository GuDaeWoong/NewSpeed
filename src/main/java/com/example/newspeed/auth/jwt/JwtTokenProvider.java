package com.example.newspeed.auth.jwt;

import com.example.newspeed.auth.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final JwtTokenUtils jwtTokenUtils;
    private final TokenBlacklistService tokenBlacklistService;

    //토큰 유지 시간 (1시간)
    private final long tokenValidityInMilliseconds = 1000 * 60 * 60;

    //Access Token : 로그인 후 API 호출에 사용
    public String createAccessToken(Long userId) {
        return jwtTokenUtils.createToken(userId, tokenValidityInMilliseconds/4); // 15분
    }

    //Refresh Token : Access Token 이 만료됐을 때 재발급 요청에 사용
    public String createRefreshToken(Long userId) {
        return jwtTokenUtils.createToken(userId, tokenValidityInMilliseconds * 168); // 일주일
    }

    public Long extractUserId(String token) {
        return jwtTokenUtils.getUserIdByToken(token);
    }

    public boolean validateToken(String token) {
        return jwtTokenUtils.isValidToken(token);
    }
    
    //로그인 상태 확인
    public boolean isLoggedIn(String accessToken) {
        // null 체크 및 블랙 리스트 확인
        if(accessToken == null) return false;
        return !tokenBlacklistService.isTokenInBlackList(accessToken);
    }

}
