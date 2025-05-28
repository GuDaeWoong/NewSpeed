package com.example.newspeed.global.common;

import com.example.newspeed.user.entity.Token;
import com.example.newspeed.user.repository.TokenRepository;
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
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //토큰 얻기
        String accessToken = jwtTokenProvider.extractAccessTokenFromHeader(request).orElse(null);
        String refreshToken = jwtTokenProvider.extractRefreshTokenFromCookie(request).orElse(null);

        if(accessToken == null && refreshToken == null) {
            filterChain.doFilter(request,response);
            return;
        }

        //토큰 유효성 검증 (Access token)
        if(jwtTokenProvider.validateToken(accessToken)){
            // 토큰이 유효하면 토큰으로부터 유저 정보 받기
            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
            // SecurityContext 에 Authentication 객체를 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }else{
            //토큰 유효성 검증 (refreshToken)
            if(jwtTokenProvider.validateToken(refreshToken)){
                Token dbToken = tokenRepository.findByRefreshToken(refreshToken).orElse(null);
                if(dbToken != null){
                    // 토큰이 유효하면 토큰으로부터 유저 정보 받기
                    Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
                    // SecurityContext 에 Authentication 객체를 저장
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    //refresh token 에서 userId 추출 → 새로운 access token 발급
                    Long userId = jwtTokenProvider.getUserIdByToken(refreshToken);

                    //발급 받은 토큰 리턴
                    jwtTokenProvider.createAccessToken(userId);
                }
            }
        }

        filterChain.doFilter(request,response);
    }
}
