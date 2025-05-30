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

    //Get 방식일때만 로그아웃 허용
    private static final String ONLY_GET_PUBLIC_URI = "/api/posts";

    //로그아웃 상태 진입 URI - 게시글 보기는 로그아웃 상태에서도 진입 가능
    private static final String[] PUBLIC_URIS = {
            "/api/users/signup",
            "/api/login"

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
        if(isLoggedIn){
            if(isOnlyLogoutUris(request, response)) return true;
        }
        if(isPublicUris(request, response)) return true;
        if(isPublicGetUris(request, response)) return true;
        return false;
    }

    //로그아웃 상태에서만 집입가능한 uri
    public boolean isOnlyLogoutUris(HttpServletRequest request,HttpServletResponse response) throws IOException{
        if (isWhitelistedUri(LOGOUT_ONLY_URIS, request.getRequestURI())) {
            filterException.writeExceptionResponse(response);
            return false;
        }
        return true;
    }

    //로그인 없이 진입가능한 uri
    public boolean isPublicUris(HttpServletRequest request, HttpServletResponse response) throws IOException{
        if (!isWhitelistedUri(PUBLIC_URIS, request.getRequestURI())) {
            filterException.writeExceptionResponse(response);
            return false;
        }
        return true;
    }

    //로그인 없이 진입가능한 Get uri (post)
    private boolean isPublicGetUris(HttpServletRequest request, HttpServletResponse response) throws IOException{
        if (request.getRequestURI().startsWith(ONLY_GET_PUBLIC_URI) && !"GET".equalsIgnoreCase(request.getMethod())) {
            filterException.writeExceptionResponse(response);
            return false;
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
