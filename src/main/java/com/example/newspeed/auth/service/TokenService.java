package com.example.newspeed.auth.service;

import com.example.newspeed.auth.dto.TokenDto;
import com.example.newspeed.auth.jwt.JwtTokenProvider;
import com.example.newspeed.auth.repository.TokenRepository;
import com.example.newspeed.global.Enums.ErrorCode;
import com.example.newspeed.global.error.CustomException;
import com.example.newspeed.auth.entity.Token;
import com.example.newspeed.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenRepository;

    // 로그인 시 토큰 생성 및 저장
    public TokenDto createAndSaveTokens(User user) {
        //토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        //refresh 토큰 DB 저장
        tokenRepository.save(new Token(user, refreshToken));

        return new TokenDto(accessToken, refreshToken);
    }


    public Long getUserIdFromRefreshToken(String refreshToken){
        //DB 에 저장된 토큰과 일치하는지 확인 -> 없으면 예외
        Optional<Token> token = tokenRepository.findByRefreshToken(refreshToken);

        if(token.isEmpty()) return null;

        return jwtTokenProvider.extractUserId(refreshToken);
    }

    // Refresh 토큰으로 Access 토큰 재발급
    public String reissueAccessToken(Long userId) {

        //새로운 access token 발급
        return jwtTokenProvider.createAccessToken(userId);
    }

    //DB 에서 Refresh 토큰 삭제
    public void deleteRefreshTokenDB(String refreshToken) {
        tokenRepository.findByRefreshToken(refreshToken)
                .ifPresent(tokenRepository::delete);
    }

}
