package com.example.newspeed.global.error;

import com.example.newspeed.global.Enums.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {
    //에러코드와 에러메세지 커스텀으로 만들어서 예외처리

    private final HttpStatus status;

    //에러 메세지 양식 직접 날려준다.
    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.status = errorCode.getStatus();
    }
}
