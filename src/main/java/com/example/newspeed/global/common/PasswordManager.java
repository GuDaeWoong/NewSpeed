package com.example.newspeed.global.common;

import com.example.newspeed.global.Enums.ErrorCode;
import com.example.newspeed.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordManager {

    private final PasswordEncoder passwordEncoder;

    //비밀번호 인코딩
    public String encodePassword(String rawPassword){

        return passwordEncoder.encode(rawPassword);
    }

    //인코딩된 비밀번호 확인(입력받은 비밀번호, DB 저장 비밀번호)
    public void validatePasswordMatchOrThrow(String inputPassword, String encodedPassword){
        if(!passwordEncoder.matches(inputPassword, encodedPassword)){
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
    }
}
