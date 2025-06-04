package com.example.newspeed.global.error;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// 이 클래스가 전역 예외 처리자임을 나타냅니다.
@ControllerAdvice
public class GlobalExceptionHandler {

    // valid에서 잡은 검증 오류 시 예외 처리
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ValidExceptionDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> errorList = new ArrayList<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errorList.add(fieldError.getField() + ": " + fieldError.getDefaultMessage());
        }

        ValidExceptionDto exceptionDto = new ValidExceptionDto(errorList);

        return ResponseEntity.status(exceptionDto.getStatus()).body(exceptionDto);
    }

    //커스텀 예외 처리기
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionDto> handleCustomException(CustomException e){
        ExceptionDto exceptionDto = new ExceptionDto(e.getStatus(), e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(exceptionDto);
    }
}