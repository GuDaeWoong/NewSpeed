package com.example.newspeed.auth.filter;

import com.example.newspeed.auth.jwt.JwtAuthenticationProvider;
import com.example.newspeed.auth.jwt.JwtTokenProvider;
import com.example.newspeed.auth.jwt.TokenExtractor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final WhiteListManager whiteListManager;
    private final TokenExtractor tokenExtractor;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //토큰 검증 무시 리스트 확인
        if(whiteListManager.isNoAuthRequiredUris(request)) {filterChain.doFilter(request,response); return;}

        //헤더에서 토큰 생성
        String accessToken = tokenExtractor.extractAccessTokenFromHeader(request).orElse(null);

        //로그인 상태 확인 (유효성 검사, 블랙리스트 확인
        boolean isLoggedIn = jwtTokenProvider.isLoggedIn(accessToken);

        //로그인 상태와 화이트리스트 판별하여 조건에 따라 예외처리.
        if(!whiteListManager.isWhiteList(isLoggedIn, request, response)) return;

        //로그인 상태라면 access 토큰 security 저장. 로그아웃 상태라면 clear
        jwtAuthenticationProvider.setTokenToSecurityContextOrClear(accessToken, isLoggedIn);

        // 다음 필터로 넘김
        filterChain.doFilter(request,response);

    }

}
