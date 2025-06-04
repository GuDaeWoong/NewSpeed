package com.example.newspeed.global.error;

import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;

@JsonPropertyOrder({"status", "message", "fieldError"})
@Getter
public class ValidExceptionDto {
	//에러코드로 관리
	private final int status = HttpStatus.BAD_REQUEST.value();

	//에러메세지
	private final String message = "올바르지 않은 요청입니다. 값을 비워두거나 유효한 형식인지 확인해주세요.";

	// 상세 오류 필드
	private final List<String> fieldError;

	public ValidExceptionDto(List<String> fieldError) {
		this.fieldError = fieldError;
	}
}
