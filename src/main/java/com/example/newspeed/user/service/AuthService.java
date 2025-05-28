package com.example.newspeed.user.service;

import com.example.newspeed.global.common.JwtTokenProvider;
import com.example.newspeed.global.common.PasswordManager;
import com.example.newspeed.user.dto.LoginRequestDto;
import com.example.newspeed.user.dto.TokenResponse;
import com.example.newspeed.user.entity.Token;
import com.example.newspeed.user.entity.User;
import com.example.newspeed.user.repository.LoginRepository;
import com.example.newspeed.user.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final LoginRepository loginRepository;
    private final PasswordManager passwordManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenRepository;

    public TokenResponse login(LoginRequestDto requestDto) {

        User user = loginRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));

        //passwordManager.validatePasswordMatchOrThrow(requestDto.getPassword(), user.getPassword());

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
        Token tokenEntity = tokenRepository.findByRefreshToken(refreshToken)
                .orElse(null);
        if (tokenEntity == null) {
            return null;
        }

        // 3. refresh token 에서 userId 추출 → 새로운 access token 발급
        Long userId = jwtTokenProvider.getUserIdByToken(refreshToken);

        return jwtTokenProvider.createAccessToken(userId);
    }
}
