package com.example.newspeed.auth.service;

import com.example.newspeed.auth.repository.TokenBlackListRepository;
import com.example.newspeed.user.entity.TokenBlackList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

//블랙리스트 확인
@Component
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final TokenBlackListRepository tokenBlackListRepository;

    //토큰 블랙리스트인지 확인
    public boolean isTokenInBlackList(String accessToken){
        return tokenBlackListRepository.existsByAccessToken(accessToken);
    }

    //Access 토큰 블랙리스트 등록
    public boolean isAccessTokenBlackListOrSave(String accessToken) {

        if(accessToken == null || tokenBlackListRepository.existsByAccessToken(accessToken)) return false;

        tokenBlackListRepository.save(new TokenBlackList(accessToken));
        return true;
    }
}
