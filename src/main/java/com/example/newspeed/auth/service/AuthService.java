package com.example.newspeed.auth.service;

import com.example.newspeed.auth.jwt.TokenCookieUtils;
import com.example.newspeed.auth.jwt.TokenExtractor;
import com.example.newspeed.global.Enums.ErrorCode;
import com.example.newspeed.global.common.PasswordManager;
import com.example.newspeed.global.error.CustomException;
import com.example.newspeed.user.dto.LoginRequestDto;
import com.example.newspeed.auth.dto.TokenResponseDto;
import com.example.newspeed.user.entity.User;
import com.example.newspeed.user.repository.LoginRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final LoginRepository loginRepository;
    private final PasswordManager passwordManager;
    private final TokenBlacklistService tokenBlacklistService;
    private final TokenExtractor tokenExtractor;
    private final TokenCookieUtils tokenCookieUtils;
    private final TokenService tokenService;

    //로그인
    public TokenResponseDto login(LoginRequestDto requestDto) {

        User user = loginRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));

        passwordManager.validatePasswordMatchOrThrow(requestDto.getPassword(), user.getPassword());

        return tokenService.createAndSaveTokens(user);
    }

    //토큰 재발급
    public String reissueAccessToken(HttpServletRequest request) {

        //쿠키에서 refresh token 꺼내기
        String refreshToken = tokenExtractor.extractRefreshTokenFromCookie(request)
                .orElseThrow(()-> new CustomException(ErrorCode.REQUIRED_LOGIN));

        //발급 받은 토큰 리턴
        return tokenService.reissueAccessToken(refreshToken);
    }

    //로그아웃 기능
    public void logout(HttpServletRequest request, HttpServletResponse response) {

        //Header 에서 Token 가져오기
        String accessToken = tokenExtractor.extractAccessTokenFromHeader(request).orElse(null);
        String refreshToken = tokenExtractor.extractRefreshTokenFromCookie(request).orElse(null);

        //accessToken 블랙리스트 추가 -> 이미 추가되어 있거나 null 일 경우 예외 처리(비정상 접근)
        if(!tokenBlacklistService.isAccessTokenBlackListOrSave(accessToken)) {
            throw new CustomException(ErrorCode.INVALID_ACCESS);
        }

        //토큰 있을 시 실행
        if(refreshToken != null){
            //DB 에서 refreshToken 삭제
            tokenService.deleteRefreshTokenDB(refreshToken);
            //쿠키에서 refreshToken 리셋
            tokenCookieUtils.deleteRefreshTokenCookie(response);
        }

    }
}
