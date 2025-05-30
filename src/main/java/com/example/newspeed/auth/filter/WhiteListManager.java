package com.example.newspeed.auth.filter;

import com.example.newspeed.global.error.FilterException;
import jakarta.servlet.http.HttpServletRequest;
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
            "/api/login",
            "/api/reissue",
            "/api/posts"
    };

    //로그인 상태에서 진입 불가 URI - 회원가입, 로그인 기능은 로그아웃 상태에서만 진입 가능
    private static final String[] LOGOUT_ONLY_URIS = {
            "/api/users/signup",
            "/api/login"
    };

    //토큰 유효성 검사 무시하는 URI
    private static final String[] NO_AUTH_REQUIRED_URIS = {
            "/api/reissue",
            "/api/logout"
    };

    //화이트 리스트 판별
    public boolean isWhiteList(boolean isLoggedIn, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(!isOnlyLogoutUris(isLoggedIn, request, response)) return false;
        if(!isPublicUris(isLoggedIn, request, response)) return false;
        return true;
    }

    //로그아웃 상태에서만 집입가능한 uri
    public boolean isOnlyLogoutUris(boolean isLoggedIn,HttpServletRequest request,HttpServletResponse httpResponse) throws IOException{
        if (isLoggedIn && isWhitelistedUri(LOGOUT_ONLY_URIS, request.getRequestURI())) {
            filterException.writeExceptionResponse(httpResponse);
            return false;
        }
        return true;
    }

    //로그인 없이 진입가능한 uri
    public boolean isPublicUris(boolean isLoggedIn, HttpServletRequest request, HttpServletResponse httpResponse) throws IOException{
        //Get 방식의 조회만 필터 제외하고, 다른 방식은(수정,삭제 등) 필터에 걸리도록 수정
        if (!isLoggedIn) {
            if (request.getMethod().startsWith("/api/posts/")) {
                if (!"GET".equalsIgnoreCase(request.getRequestURI())) { //대소문자 구분없이 판단
                    filterException.writeExceptionResponse(httpResponse);
                    return false;
                }
            } else if (!isWhitelistedUri(PUBLIC_URIS, request.getRequestURI())) {
                filterException.writeExceptionResponse(httpResponse);
                return false;
            }
        }
        return true;
    }

    
    //유효성 검증 무시 uri
    public boolean isNoAuthRequiredUris(HttpServletRequest request) {
        return isWhitelistedUri(NO_AUTH_REQUIRED_URIS, request.getRequestURI());
    }

    //화이트 리스트 인지 확인
    private boolean isWhitelistedUri(String[] URLS_LIST, String requestURI) {

        return PatternMatchUtils.simpleMatch(URLS_LIST, requestURI);
    }
}
