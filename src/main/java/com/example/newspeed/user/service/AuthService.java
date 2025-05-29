package com.example.newspeed.user.service;

import com.example.newspeed.global.common.JwtTokenProvider;
import com.example.newspeed.global.common.PasswordManager;
import com.example.newspeed.user.dto.LoginRequestDto;
import com.example.newspeed.user.dto.TokenResponse;
import com.example.newspeed.user.entity.Token;
import com.example.newspeed.user.entity.TokenBlackList;
import com.example.newspeed.user.entity.User;
import com.example.newspeed.user.repository.LoginRepository;
import com.example.newspeed.user.repository.TokenBlackListRepository;
import com.example.newspeed.user.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final LoginRepository loginRepository;
    private final PasswordManager passwordManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenRepository;
    private final TokenBlackListRepository tokenBlackListRepository;

    public TokenResponse login(LoginRequestDto requestDto) {

        User user = loginRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));

        passwordManager.validatePasswordMatchOrThrow(requestDto.getPassword(), user.getPassword());

        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        tokenRepository.save(new Token(user, refreshToken));
        return new TokenResponse(accessToken, refreshToken);
    }

    public String reissueAccessToken(HttpServletRequest request) {



        //쿠키에서 refresh token 꺼내기
        String refreshToken = jwtTokenProvider.extractRefreshTokenFromCookie(request)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));


        //DB 에 저장된 토큰과 일치하는지 확인
        Optional<Token> tokenEntity = tokenRepository.findByRefreshToken(refreshToken);
        if (tokenEntity.isEmpty()) {
            return null;
        }

        //refresh token 에서 userId 추출 → 새로운 access token 발급
        Long userId = jwtTokenProvider.getUserIdByToken(refreshToken);

        //발급 받은 토큰 리턴
        return jwtTokenProvider.createAccessToken(userId);
    }

    //Refresh 토큰 삭제
    public void deleteRefreshTokenDB(HttpServletRequest request) {
        //Cookie 에서 refreshToken 가져오기
        String refreshToken = jwtTokenProvider.extractRefreshTokenFromCookie(request).orElse(null);

        if(refreshToken == null) return;

        Optional<Token> findToken = tokenRepository.findByRefreshToken(refreshToken);
        findToken.ifPresent(tokenRepository::delete);
    }

    //Access 토큰 블랙리스트 등록
    public boolean addAccessTokenToBlackList(HttpServletRequest request) {

        //Header 에서 accessToken 가져오기
        String accessToken = jwtTokenProvider.extractAccessTokenFromHeader(request).orElse(null);

        if(accessToken == null || tokenBlackListRepository.existsByAccessToken(accessToken)) return false;

        tokenBlackListRepository.save(new TokenBlackList(accessToken));
        return true;
    }
}
