package com.example.newspeed.user.service;

import com.example.newspeed.global.Enums.ErrorCode;
import com.example.newspeed.global.common.JwtTokenProvider;
import com.example.newspeed.global.common.PasswordManager;
import com.example.newspeed.global.error.CustomException;
import com.example.newspeed.user.dto.LoginRequestDto;
import com.example.newspeed.user.dto.TokenResponseDto;
import com.example.newspeed.user.entity.Token;
import com.example.newspeed.user.entity.TokenBlackList;
import com.example.newspeed.user.entity.User;
import com.example.newspeed.user.repository.LoginRepository;
import com.example.newspeed.user.repository.TokenBlackListRepository;
import com.example.newspeed.user.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    public TokenResponseDto login(LoginRequestDto requestDto) {

        User user = loginRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));

        passwordManager.validatePasswordMatchOrThrow(requestDto.getPassword(), user.getPassword());

        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        tokenRepository.save(new Token(user, refreshToken));

        return new TokenResponseDto(accessToken, refreshToken);
    }

    public String reissueAccessToken(HttpServletRequest request) {

        //쿠키에서 refresh token 꺼내기
        String refreshToken = jwtTokenProvider.extractRefreshTokenFromCookie(request)
                .orElseThrow(()-> new CustomException(ErrorCode.REQUIRED_LOGIN));

        //DB 에 저장된 토큰과 일치하는지 확인 -> 없으면 예외
        Token tokenEntity = tokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(()-> new CustomException(ErrorCode.REQUIRED_LOGIN));

        //refresh token 에서 userId 추출 → 새로운 access token 발급
        Long userId = jwtTokenProvider.getUserIdByToken(refreshToken);

        //발급 받은 토큰 리턴
        return jwtTokenProvider.createAccessToken(userId);
    }

    //DB 에서 Refresh 토큰 삭제
    private void deleteRefreshTokenDB(String refreshToken) {
        Optional<Token> findToken = tokenRepository.findByRefreshToken(refreshToken);
        findToken.ifPresent(tokenRepository::delete);
    }

    //Access 토큰 블랙리스트 등록
    private boolean isAccessTokenBlackListOrSave(String accessToken) {

        if(accessToken == null || tokenBlackListRepository.existsByAccessToken(accessToken)) return false;

        tokenBlackListRepository.save(new TokenBlackList(accessToken));
        return true;
    }

    //로그아웃 기능
    public void logout(HttpServletRequest request, HttpServletResponse response) {

        //Header 에서 Token 가져오기
        String accessToken = jwtTokenProvider.extractAccessTokenFromHeader(request).orElse(null);
        String refreshToken = jwtTokenProvider.extractRefreshTokenFromCookie(request).orElse(null);

        //accessToken 블랙리스트 추가 -> 이미 추가되어 있거나 null 일 경우 예외 처리(비정상 접근)
        if(!isAccessTokenBlackListOrSave(accessToken)) throw new CustomException(ErrorCode.INVALID_ACCESS);

        //토큰 있을 시 실행
        if(refreshToken != null){

            //DB 에서 refreshToken 삭제
            deleteRefreshTokenDB(refreshToken);

            //쿠키에서 refreshToken 리셋
            jwtTokenProvider.deleteRefreshTokenCookie(response);
        }

    }
}
