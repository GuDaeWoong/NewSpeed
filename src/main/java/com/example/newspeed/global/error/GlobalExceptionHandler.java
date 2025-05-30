package com.example.newspeed.global.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 이 클래스가 전역 예외 처리자임을 나타냅니다.
@ControllerAdvice
public class GlobalExceptionHandler {

    // valid에서 잡은 검증 오류 시 예외 처리
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<Map<String, String>> errorList = new ArrayList<>();

        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            Map<String, String> error = new HashMap<>();
            error.put("field", fieldError.getField());
            error.put("message", fieldError.getDefaultMessage());
            errorList.add(error);
        }

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "올바르지 않은 요청입니다. 값을 비워두거나 유효한 형식인지 확인해주세요.");
        responseBody.put("errors", errorList);

        return new ResponseEntity<>(responseBody.toString(), HttpStatus.BAD_REQUEST);
    }

    //커스텀 예외 처리기
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionDto> handleCustomException(CustomException e){
        ExceptionDto exceptionDto = new ExceptionDto(e.getStatus(), e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(exceptionDto);
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<String> handleCommentNotFoundException(CommentNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<String> handleUnauthorizedAccessException(UnauthorizedAccessException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<String> handlePasswordMismatchException(PasswordMismatchException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }
}