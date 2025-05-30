package com.example.newspeed.user.controller;

import com.example.newspeed.global.common.JwtTokenProvider;
import com.example.newspeed.user.dto.AccessTokenResponseDto;
import com.example.newspeed.user.dto.CustomUserDetails;
import com.example.newspeed.user.dto.LoginRequestDto;
import com.example.newspeed.user.dto.TokenResponseDto;
import com.example.newspeed.user.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto,
                                                        HttpServletResponse response){

        //비밀번호 확인 후 토큰 변환 후 반환
        TokenResponseDto token = authService.login(requestDto);

        // refresh 토큰을 쿠키에 추가
        jwtTokenProvider.addRefreshTokenToCookie(token.getRefreshToken(), response);

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

        String accessToken = jwtTokenProvider.extractAccessTokenFromHeader(request).orElse(null);
        if(!jwtTokenProvider.validateToken(accessToken)) accessToken = "유효기간만료";
        String refreshToken = jwtTokenProvider.extractRefreshTokenFromCookie(request).orElse(null);

        Long id = jwtTokenProvider.getUserIdFromSecurity();
        String userId = userDetails.getUsername();


        new TokenResponseDto(accessToken, refreshToken);

        return new ResponseEntity<>(new TokenResponseDto(accessToken, refreshToken), HttpStatus.OK);
    }


}
