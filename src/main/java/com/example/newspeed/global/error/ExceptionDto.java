package com.example.newspeed.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ExceptionDto {
    //에러코드로 관리
    private final int status;

    //에러메세지
    private final String message;

    public ExceptionDto(HttpStatus status, String message) {
        this.status = status.value();
        this.message = message;
    }
}
