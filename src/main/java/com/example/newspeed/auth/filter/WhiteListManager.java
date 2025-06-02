package com.example.newspeed.auth.filter;

import com.example.newspeed.global.error.FilterException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class WhiteListManager {

    private final FilterException filterException;

    //Get 방식일때만 로그아웃 허용 -> URI 추가 고려하여 배열로 생성
    private static final String[] ONLY_GET_PUBLIC_URI = {
            "/api/posts"
    };

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
        String requestUri = request.getRequestURI();
        String requestMethod = request.getMethod();

        //로그인 상태
        if(isLoggedIn){
            return !isLogoutOnlyUris(requestUri, response);
            //비로그인 상태
        }else{
            if(isPublicUris(requestUri, response)) return true;
            if(isPublicGetUris(requestUri,requestMethod , response)) return true;
        }
        filterException.writeExceptionResponse(response);
        return false;
    }

    //로그아웃 상태에서만 집입가능한 uri
    public boolean isLogoutOnlyUris(String requestUri, HttpServletResponse response) throws IOException{
        return isWhitelistedUri(LOGOUT_ONLY_URIS, requestUri);
    }

    //로그인 없이 진입가능한 uri -> 추후 사용
    public boolean isPublicUris(String requestUri, HttpServletResponse response) throws IOException{
        return isWhitelistedUri(PUBLIC_URIS, requestUri);
    }

    //로그인 없이 진입가능한 Get uri (post)
    private boolean isPublicGetUris(String requestUri,String requestMethod , HttpServletResponse response) throws IOException{
        boolean isWhiteList = isStartsWithWhitelistedUri(ONLY_GET_PUBLIC_URI, requestUri);
        return isWhiteList && "GET".equalsIgnoreCase(requestMethod);
    }

    //유효성 검증 무시 uri
    public boolean isNoAuthRequiredUris(HttpServletRequest request) {
        return isWhitelistedUri(NO_AUTH_REQUIRED_URIS, request.getRequestURI());
    }

    //화이트 리스트 인지 확인
    private boolean isWhitelistedUri(String[] URLS_LIST, String requestURI) {
        return PatternMatchUtils.simpleMatch(URLS_LIST, requestURI);
    }

    //하위 경로 포함하여 화이트 리스트 확인
    private boolean isStartsWithWhitelistedUri (String[] URLS_LIST, String requestURI){
        return Arrays.stream(URLS_LIST)
                .anyMatch(requestURI::startsWith);
    }
}
