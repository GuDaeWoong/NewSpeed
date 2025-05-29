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
    private final WhiteListManager whiteListManager;
    private final FilterException filterException;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //토큰 얻기
        String accessToken = jwtTokenProvider.extractAccessTokenFromHeader(request).orElse(null);
        String refreshToken = jwtTokenProvider.extractRefreshTokenFromCookie(request).orElse(null);

        //로그인 상태 확인
        boolean isLoggedIn = refreshToken != null;

        //로그인 상태와 화이트리스트 판별하여 조건에 따라 예외처리.
        whiteListManager.validateWhitelistAccess(isLoggedIn, request, response);

        //refresh token 이 null 일 경우 바로 다음 필터 진입
        if(!isLoggedIn) {
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request,response);
            return;
        }

        //토큰 유효성 검증 (Access token)
        if(accessToken != null && jwtTokenProvider.validateToken(accessToken)){
            // 토큰으로부터 유저 정보 받기
            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
            // SecurityContext 에 Authentication 객체를 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

        }
        //토큰 유효성 재검증 (refreshToken) -> 인증 성공 시 access 토큰 새로 발급
        else if(jwtTokenProvider.validateToken(refreshToken)){
                Token dbToken = tokenRepository.findByRefreshToken(refreshToken).orElse(null);
                if(dbToken != null){

                    //refresh token 에서 userId 추출
                    Long userId = jwtTokenProvider.getUserIdByToken(refreshToken);

                    //새로운 access token 발급
                    String newAccessToken = jwtTokenProvider.createAccessToken(userId);

                    //토큰으로부터 Authentication 생성
                    Authentication authentication = jwtTokenProvider.getAuthentication(newAccessToken);
                    
                    // SecurityContext 에 Authentication 객체를 저장
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    //새로운 토큰 헤더에 포함
                    response.setHeader("Authorization", "Bearer " + newAccessToken);
                }else{
                    SecurityContextHolder.clearContext();
                    //refresh 토큰이 db 에 없음 -> 보안상 위험. 토큰 삭제
                    jwtTokenProvider.deleteRefreshToken(response);
                    //body 로 재로그인 요청 및 에러처리
                }
        }else{
            SecurityContextHolder.clearContext();
            //refresh 토큰이 db 에 없음 -> 보안상 위험. 토큰 삭제
            jwtTokenProvider.deleteRefreshToken(response);
            //body 로 재로그인 요청 및 에러처리
        }
        filterChain.doFilter(request,response);
    }
}
