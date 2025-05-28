package com.example.newspeed.global.common;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class WhiteListManager {

    private final FilterException filterException;

    //로그아웃 상태 진입 URI - 게시글 보기는 로그아웃 상태에서도 진입 가능
    private static final String[] PUBLIC_URIS = {
            "/api/users/signup",
            "/api/users/login",
            "/api/posts"
    };

    //로그인 상태에서 진입 불가 URI - 회원가입, 로그인 기능은 로그아웃 상태에서만 진입 가능
    private static final String[] LOGOUT_ONLY_URIS = {
            "/api/users/signup",
            "/api/users/login"
    };

    //로그인 상태와 화이트리스트 판별하여 조건에 따라 예외처리.
    public boolean validateWhitelistAccess(boolean isLoggedIn, String requestURI, String httpMethod, HttpServletResponse httpResponse) throws IOException {
        if (isLoggedIn && isWhitelistedUri(LOGOUT_ONLY_URIS, requestURI)) {
            filterException.writeExceptionResponse(httpResponse);
            return false;
        }

        //Get 방식의 조회만 필터 제외하고, 다른 방식은(수정,삭제 등) 필터에 걸리도록 수정
        if (!isLoggedIn) {
            if (requestURI.startsWith("/api/posts/")) {
                if (!"GET".equalsIgnoreCase(httpMethod)) { //대소문자 구분없이 판단
                    filterException.writeExceptionResponse(httpResponse);
                    return false;
                }
            } else if (!isWhitelistedUri(PUBLIC_URIS, requestURI)) {
                filterException.writeExceptionResponse(httpResponse);
                return false;
            }
        }
        return true;
    }

    //화이트 리스트 인지 확인
    private boolean isWhitelistedUri(String[] URLS_LIST, String requestURI) {

        return PatternMatchUtils.simpleMatch(URLS_LIST, requestURI);
    }
}
