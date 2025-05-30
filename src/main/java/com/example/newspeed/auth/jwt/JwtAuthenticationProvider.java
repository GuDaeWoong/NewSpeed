package com.example.newspeed.auth.jwt;

import com.example.newspeed.global.Enums.ErrorCode;
import com.example.newspeed.global.error.CustomException;
import com.example.newspeed.global.error.FilterException;
import com.example.newspeed.user.dto.CustomUserDetails;
import com.example.newspeed.user.entity.User;
import com.example.newspeed.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

//Authentication 생성
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final FilterException filterException;

    //토큰으로부터 유저 정보를 받기
    private Authentication getAuthentication(String token) {
        Long userId = jwtTokenProvider.extractUserId(token);

        // DB 에서 사용자 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // CustomUserDetails 생성
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        return new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
    }

    //Security 에서 userId 생성
    public Long getUserIdFromSecurity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }
        try {
            return (Long) authentication.getPrincipal();
        } catch (ClassCastException e) {
            return null;
        }
    }

    //토큰에서 인증 정보 설정
    public void saveAuthenticationFromToken(String accessToken){
        // 토큰으로부터 유저 정보 받기
        Authentication authentication = getAuthentication(accessToken);
        // SecurityContext 에 Authentication 객체를 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


    public void setTokenToSecurityContextOrClear(String accessToken, boolean isLoggedIn, HttpServletResponse response) throws IOException {
        //accessToken 이 null 일 경우 Security 초기화
        if(!isLoggedIn) {
            SecurityContextHolder.clearContext();
        }
        //토큰 유효성 검증 후 SecurityContext 저장 (Access token)
        else{
            validateTokenToSaveToken(accessToken,response);
        }
    }

    //토큰 유효성 검증 Or 예외 처리 (Access token)
    private void validateTokenToSaveToken(String accessToken, HttpServletResponse response) throws IOException{
        if(jwtTokenProvider.validateToken(accessToken)){
            //인증 정보 저장
            saveAuthenticationFromToken(accessToken);
        }else{
            // 토큰 재발급 필요
            filterException.writeExceptionResponse(response);
        }
    }

}
