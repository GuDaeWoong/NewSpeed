package com.example.newspeed.auth.service;

import com.example.newspeed.auth.dto.AccessTokenDto;
import com.example.newspeed.global.Enums.ErrorCode;
import com.example.newspeed.global.common.PasswordManager;
import com.example.newspeed.global.error.CustomException;
import com.example.newspeed.user.dto.LoginRequestDto;
import com.example.newspeed.auth.dto.TokenDto;
import com.example.newspeed.user.entity.User;
import com.example.newspeed.user.repository.LoginRepository;
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
    private final TokenService tokenService;

    //로그인
    public TokenDto login(LoginRequestDto requestDto) {

        User user = loginRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));

        passwordManager.validatePasswordMatchOrThrow(requestDto.getPassword(), user.getPassword());

        return tokenService.createAndSaveTokens(user);
    }

    //로그아웃 기능
    public void logout(TokenDto tokenDto) {

        //accessToken 블랙리스트 추가 -> 이미 추가되어 있거나 null 일 경우 예외 처리(비정상 접근)
        if(!tokenBlacklistService.isAccessTokenBlackListOrSave(tokenDto.getAccessToken())) {
            throw new CustomException(ErrorCode.INVALID_ACCESS);
        }

        //토큰 있을 시 실행
        if(tokenDto.getRefreshToken() == null) throw new CustomException(ErrorCode.INVALID_ACCESS);

        //DB 에서 refreshToken 삭제
        tokenService.deleteRefreshTokenDB(tokenDto.getRefreshToken());

    }

    //토큰 재발급
    public AccessTokenDto reissueAccessToken(String refreshToken) {

        //Refresh 토큰으로 userId 생성
        Long userId = tokenService.getUserIdFromRefreshToken(refreshToken);

        if(userId == null) return null;

        //발급 받은 토큰 리턴
        String newAccessToken = tokenService.reissueAccessToken(userId);
        return new AccessTokenDto(newAccessToken);
    }

}
