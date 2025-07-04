package com.example.newspeed.global.Enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    //에러코드와 에러메세지 관리

    // 사용자 관련
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 사용자입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저 정보가 없습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    ALREADY_LOGGED_IN(HttpStatus.CONFLICT, "이미 로그인된 사용자입니다."),
    CANNOT_FOLLOW_SELF(HttpStatus.BAD_REQUEST, "같은 유저는 팔로우/언팔로우 할 수 없습니다."),
    ALREADY_FOLLOWING(HttpStatus.CONFLICT, "이미 팔로우 상태입니다."),
    NOT_FOLLOWING(HttpStatus.BAD_REQUEST, "팔로우 상태가 아닙니다."),

    // 게시글 관련
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."),
    POST_NOT_OWNED(HttpStatus.BAD_REQUEST, "본인의 게시글만 수정 및 삭제할 수 있습니다."),
    POST_NOT_CHANGE(HttpStatus.BAD_REQUEST, "변경할 내용을 입력해야 합니다."),
    POST_NOT_TITLE(HttpStatus.BAD_REQUEST,"제목을 입력해야 합니다."),
    POST_NOT_CONTENTS(HttpStatus.BAD_REQUEST,"내용을 입력해야 합니다."),
    POST_NOT_IMAGE(HttpStatus.BAD_REQUEST,"이미지를 삽입해야 합니다."),
    TITLE_LENGTH_OVER(HttpStatus.BAD_REQUEST, "제목은 255자까지 입력 가능합니다."),

    //좋아요 관련
    ALREADY_LIKE(HttpStatus.CONFLICT, "이미 좋아요 상태입니다."),
    NOT_LIKE(HttpStatus.BAD_REQUEST, "좋아요 상태가 아닙니다."),
    
    // 댓글 관련
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 댓글을 찾을 수 없습니다."),
    COMMENT_NOT_OWNED(HttpStatus.BAD_REQUEST, "본인의 댓글만 삭제할 수 있습니다."),

    // 일정 관련
    INVALID_SCHEDULE_OWNER(HttpStatus.BAD_REQUEST, "해당 사용자의 일정이 아닙니다."),
    UNAUTHORIZED_SCHEDULE_ACCESS(HttpStatus.UNAUTHORIZED, "해당 사용자의 일정이 아닙니다."),

    // 요청 관련
    BAD_REQUEST_CONTENT(HttpStatus.CONFLICT, "해당 내용은 추가할 수 없습니다."),

    // 인증 관련
    REQUIRED_LOGIN(HttpStatus.BAD_REQUEST, "로그인이 필요합니다."),
    REQUIRED_LOGOUT(HttpStatus.BAD_REQUEST, "로그인 상태에서는 할 수 없습니다."),
    INVALID_ACCESS(HttpStatus.FORBIDDEN, "잘못된 접근입니다."),

    // 페이징 관련
    PAGE_NOT_FOUND(HttpStatus.FORBIDDEN, "요청하신 페이지가 존재하지 않습니다.");

    private final HttpStatus status;
    private final String message;
}
