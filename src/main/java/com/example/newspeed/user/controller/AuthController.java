package com.example.newspeed.user.controller;

import com.example.newspeed.global.common.JwtTokenProvider;
import com.example.newspeed.user.dto.LoginRequestDto;
import com.example.newspeed.user.dto.TokenResponse;
import com.example.newspeed.user.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequestDto requestDto,
                                      HttpServletResponse response){

        //비밀번호 확인 후 토큰 변환 후 반환
        TokenResponse token = authService.login(requestDto);

        // refresh 토큰을 쿠키에 추가
        jwtTokenProvider.addRefreshTokenToCookie(token.getRefreshToken(), response);

        // access 토큰을 헤더에 추가 -> 포스트맨 사용시 수동으로 넣어주어야 해서 밑의 구문 사용
        //jwtTokenProvider.addAccessTokenToHeader(token.getAccessToken(), response);

        //postman 에서 사용. 헤더에 자동으로 넣어준다.
        Map<String, String> result = new HashMap<>();
        result.put("access_token", token.getAccessToken());

        return ResponseEntity.ok(result);  // 200 OK + JSON body
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request,
                                       HttpServletResponse response){

        //Header 에서 accessToken 가져오기
        String accessToken = jwtTokenProvider.extractAccessTokenFromHeader(request).orElse(null);

        //Cookie 에서 refreshToken 가져오기
        String refreshToken = jwtTokenProvider.extractRefreshTokenFromCookie(request).orElse(null);
        
        //accessToken 블랙리스트 추가
        authService.addAccessTokenToBlackList(accessToken);

        //DB 에서 refreshToken 삭제
        authService.deleteRefreshTokenDB(refreshToken);

        //쿠키에서 refreshToken 리셋
        jwtTokenProvider.deleteRefreshTokenCookie(response);

        return new ResponseEntity<>(HttpStatus.OK);


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
