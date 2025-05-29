package com.example.newspeed.global.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final WhiteListManager whiteListManager;
    private final FilterException filterException;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //헤더에서 토큰 생성
        String accessToken = jwtTokenProvider.extractAccessTokenFromHeader(request).orElse(null);

        //로그인 상태 확인
        boolean isLoggedIn = jwtTokenProvider.isLoggedIn(accessToken);

        //로그인 상태와 화이트리스트 판별하여 조건에 따라 예외처리.
        whiteListManager.validateWhitelistAccess(isLoggedIn, request, response);

        //refresh token 이 null 일 경우 Security 초기화
        if(!isLoggedIn) {
            SecurityContextHolder.clearContext();
        }
        //재발급 요청일 경우 유효성 검증 안함
        else if(!whiteListManager.isReissueUri(request)){
            //토큰 유효성 검증 후 SecurityContext 저장 (Access token)
            validateTokenOrThrow(accessToken,response);
        }

        // 다음 필터로 넘김
        filterChain.doFilter(request,response);

    }
    

    //토큰 유효성 검증 Or 예외 처리 (Access token)
    private void validateTokenOrThrow(String accessToken,HttpServletResponse response) throws IOException{
        if(jwtTokenProvider.validateToken(accessToken)){
           //인증 정보 저장
            setAuthenticationFromToken(accessToken);
        }else{
            // 토큰 재발급 필요
            filterException.writeExceptionResponse(response);
        }
    }

    //토큰에서 인증 정보 설정
    private void setAuthenticationFromToken(String accessToken){
        // 토큰으로부터 유저 정보 받기
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        // SecurityContext 에 Authentication 객체를 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
