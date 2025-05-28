package com.example.newspeed.user.controller;

import com.example.newspeed.global.common.JwtTokenProvider;
import com.example.newspeed.user.dto.LoginRequestDto;
import com.example.newspeed.user.dto.TokenResponse;
import com.example.newspeed.user.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto requestDto,
                                      HttpServletResponse response){
        
        //비밀번호 확인 후 토큰 변환 후 반환
        TokenResponse token = authService.login(requestDto);

        // 토큰을 응답 헤더에 추가
        jwtTokenProvider.setRefreshTokenToCookie(token, response);

        return new ResponseEntity<>("Access Token : " + token.getAccessToken(), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request,
                                       HttpServletResponse response){

        //Cookie 에서 refreshToken 가져오기
        String refreshToken = jwtTokenProvider.extractRefreshTokenFromCookie(request).orElse(null);

        //DB 에서 refreshToken 삭제
        if(refreshToken != null) {
            authService.deleteRefreshToken(refreshToken);

            //클라이언트 쿠키에서 삭제 (MaxAge = 0)
            Cookie cookie = new Cookie("refreshToken", null);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
        return null;
    }

    @PostMapping("/tokentest")
    public ResponseEntity<TokenResponse> loginTest(HttpServletRequest request){

        String accessToken = jwtTokenProvider.extractAccessTokenFromHeader(request).orElse(null);
        if(!jwtTokenProvider.validateToken(accessToken)) accessToken = "유효기간만료";
        String refreshToken = jwtTokenProvider.extractRefreshTokenFromCookie(request).orElse(null);

        Long id = jwtTokenProvider.getUserIdFromSecurity();

        new TokenResponse(accessToken, refreshToken);

        return new ResponseEntity<>(new TokenResponse(accessToken, refreshToken), HttpStatus.OK);
    }

    @PostMapping("/reissue")
    public ResponseEntity<String> reissue(HttpServletRequest request) {

        String newAccessToken = authService.reissueAccessToken(request);

        return new ResponseEntity<>("Access Token : " + newAccessToken, HttpStatus.OK);
    }


}
