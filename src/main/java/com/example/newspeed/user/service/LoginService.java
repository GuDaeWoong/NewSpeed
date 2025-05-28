package com.example.newspeed.user.service;

import com.example.newspeed.global.common.JwtTokenProvider;
import com.example.newspeed.global.common.PasswordManager;
import com.example.newspeed.user.dto.CustomUserDetails;
import com.example.newspeed.user.dto.LoginRequestDto;
import com.example.newspeed.user.dto.TokenResponse;
import com.example.newspeed.user.entity.Token;
import com.example.newspeed.user.entity.User;
import com.example.newspeed.user.repository.LoginRepository;
import com.example.newspeed.user.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class LoginService {

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
}
