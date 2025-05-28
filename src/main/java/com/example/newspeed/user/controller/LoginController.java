package com.example.newspeed.user.controller;

import com.example.newspeed.global.common.JwtTokenProvider;
import com.example.newspeed.user.dto.LoginRequestDto;
import com.example.newspeed.user.dto.TokenResponse;
import com.example.newspeed.user.service.LoginService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto requestDto,
                                      HttpServletResponse response){
        
        //비밀번호 확인 후 토큰 변환 후 반환
        TokenResponse token = loginService.login(requestDto);

        // 토큰을 응답 헤더에 추가
        jwtTokenProvider.setRefreshTokenToCookie(token, response);

        return new ResponseEntity<>("Access Token : " + token.getAccessToken(), HttpStatus.OK);
    }

    @PostMapping("/logintest")
    public ResponseEntity<String> loginTest(){

        Long userId = jwtTokenProvider.getUserIdFromSecurity();

        return new ResponseEntity<>("userId : " + userId, HttpStatus.OK);
    }


}
