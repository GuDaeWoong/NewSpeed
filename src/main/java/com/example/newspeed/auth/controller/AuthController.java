package com.example.newspeed.auth.controller;

import com.example.newspeed.auth.jwt.*;
import com.example.newspeed.auth.dto.AccessTokenResponseDto;
import com.example.newspeed.auth.jwt.JwtTokenService;
import com.example.newspeed.user.dto.CustomUserDetails;
import com.example.newspeed.user.dto.LoginRequestDto;
import com.example.newspeed.auth.dto.TokenResponseDto;
import com.example.newspeed.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TokenCookieManager tokenCookieManager;
    private final TokenExtractor tokenExtractor;
    private final JwtTokenService jwtTokenService;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto,
                                                        HttpServletResponse response){

        //비밀번호 확인 후 토큰 변환 후 반환
        TokenResponseDto token = authService.login(requestDto);

        // refresh 토큰을 쿠키에 추가
        tokenCookieManager.addRefreshTokenToCookie(token.getRefreshToken(), response);

        return new ResponseEntity<>(new AccessTokenResponseDto(token.getAccessToken()),HttpStatus.OK);

    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request,
                                       HttpServletResponse response){

        authService.logout(request, response);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/reissue")
    public ResponseEntity<AccessTokenResponseDto> reissue(HttpServletRequest request) {

        String newAccessToken = authService.reissueAccessToken(request);

        return new ResponseEntity<>(new AccessTokenResponseDto(newAccessToken), HttpStatus.OK);
    }

    //토큰 확인용 테스트 코드 > 삭제예정
    @PostMapping("/tokentest")
    public ResponseEntity<TokenResponseDto> loginTest(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                      HttpServletRequest request){

        String accessToken = tokenExtractor.extractAccessTokenFromHeader(request).orElse(null);
        if(!jwtTokenService.validateToken(accessToken)) accessToken = "유효기간만료";
        String refreshToken = tokenExtractor.extractRefreshTokenFromCookie(request).orElse(null);

        Long id = jwtAuthenticationProvider.getUserIdFromSecurity();
        Long userId = userDetails.getId();


        new TokenResponseDto(accessToken, refreshToken);

        return new ResponseEntity<>(new TokenResponseDto(accessToken, refreshToken), HttpStatus.OK);
    }


}
