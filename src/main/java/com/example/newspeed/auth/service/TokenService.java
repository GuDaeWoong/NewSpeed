package com.example.newspeed.auth.service;

import com.example.newspeed.auth.dto.TokenResponseDto;
import com.example.newspeed.auth.jwt.JwtTokenProvider;
import com.example.newspeed.auth.jwt.JwtTokenService;
import com.example.newspeed.auth.repository.TokenRepository;
import com.example.newspeed.global.Enums.ErrorCode;
import com.example.newspeed.global.error.CustomException;
import com.example.newspeed.user.entity.Token;
import com.example.newspeed.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenService jwtTokenService;
    private final TokenRepository tokenRepository;

    // 로그인 시 토큰 생성 및 저장
    public TokenResponseDto createAndSaveTokens(User user) {
        //토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        //refresh 토큰 DB 저장
        tokenRepository.save(new Token(user, refreshToken));

        return new TokenResponseDto(accessToken, refreshToken);
    }

    // Refresh 토큰으로 Access 토큰 재발급
    public String reissueAccessToken(String refreshToken) {
        //DB 에 저장된 토큰과 일치하는지 확인 -> 없으면 예외
        Token tokenEntity = tokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(()-> new CustomException(ErrorCode.REQUIRED_LOGIN));

        //refresh token 에서 userId 추출 → 새로운 access token 발급
        Long userId = jwtTokenService.getUserIdByToken(refreshToken);

        return jwtTokenProvider.createAccessToken(userId);
    }

    // Refresh 토큰 삭제
    public void deleteRefreshToken(String refreshToken) {
        tokenRepository.findByRefreshToken(refreshToken)
                .ifPresent(tokenRepository::delete);
    }

    //DB 에서 Refresh 토큰 삭제
    public void deleteRefreshTokenDB(String refreshToken) {
        Optional<Token> findToken = tokenRepository.findByRefreshToken(refreshToken);
        findToken.ifPresent(tokenRepository::delete);
    }
}
