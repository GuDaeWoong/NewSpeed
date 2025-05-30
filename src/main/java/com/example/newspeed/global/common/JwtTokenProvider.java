package com.example.newspeed.global.common;

import com.example.newspeed.user.repository.TokenBlackListRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class JwtTokenProvider {
    private Key key;

    //토큰 유지 시간 (1시간)
    private final long tokenValidityInMilliseconds = 1000 * 60 * 60;

    //토큰 블랙리스트
    private final TokenBlackListRepository tokenBlackListRepository;
    public JwtTokenProvider(TokenBlackListRepository tokenBlackListRepository) {
        this.tokenBlackListRepository = tokenBlackListRepository;
    }

    @PostConstruct
    protected void init() {
        // HS256 알고리즘용 키 자동 생성
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    //Access Token : 로그인 후 API 호출에 사용
    public String createAccessToken(Long userId) {
        return createToken(userId, tokenValidityInMilliseconds);
    }

    //Refresh Token : Access Token 이 만료됐을 때 재발급 요청에 사용
    public String createRefreshToken(Long userId) {
        return createToken(userId, tokenValidityInMilliseconds * 168);
    }

    //토큰 생성
    private String createToken(Long userId, long validityInMilliseconds) {
        //JWT 의 데이터 영역 부분. userId 는 기본 필드
        Claims claims = Jwts.claims().setSubject(userId.toString());

        //현재 시간과 토큰의 만료시간 설정
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims) // claims set
                .setIssuedAt(now) // 토큰 생성 시각
                .setExpiration(validity) // 만료시각
                .signWith(key, SignatureAlgorithm.HS256) //비밀 키로 서명(HS256 알고리즘 사용)
                .compact(); // JWT 문자열로 압축(최종 토큰 생성)
    }


    // Header 에서 access token 값을 가져온다.
    public Optional<String> extractAccessTokenFromHeader(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization")) // Authorization 헤더의 값을 Optional로 감싼다.
                .filter(header -> header.startsWith("Bearer ")) // 값이 "Bearer "로 시작하는지 확인
                .map(header -> header.substring(7)); // "Bearer " 길이만큼 잘라서 리턴
    }

    // Cookie 에서 refresh token 값을 가져온다.
    public Optional<String> extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return Optional.empty();

        //쿠키에서 토큰 찾아서 유효성 검증 후 리턴
        return getRefreshTokenFromCookie(cookies);
    }

    //쿠키에서 refresh_token 이름으로 토큰 받기
    private Optional<String> getRefreshTokenFromCookie(Cookie[] cookies){
        for (Cookie cookie : cookies) {
            if ("refresh_token".equals(cookie.getName())) {
                String refreshToken = cookie.getValue();
                if(validateToken(refreshToken)){
                    return Optional.ofNullable(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }


    //토큰으로부터 유저 정보를 받기
    public Authentication getAuthentication(String token) {
        Long userId = getUserIdByToken(token);
        List<GrantedAuthority> authorities = List.of(); // 권한 없거나 기본 권한 설정
        return new UsernamePasswordAuthenticationToken(userId, null, authorities);
    }

    // 토큰에서 userId 추출
    public Long getUserIdByToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    //토큰 유효성 검증(key, 만료, 유효 시작, 형식)
    //보안상 예외 처리는 최소화
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    //Security 에서 userId 생성
    public Long getUserIdFromSecurity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }
        try {
            return (Long) authentication.getPrincipal();
        } catch (ClassCastException e) {
            return null;
        }
    }


    //refresh token 쿠키 저장
    public void addRefreshTokenToCookie(String refreshToken, HttpServletResponse response){
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();

        response.setHeader("Set-Cookie", cookie.toString());
    }

    //access token 헤더 저장
    public void addAccessTokenToHeader(String accessToken, HttpServletResponse response) {
        response.setHeader("Authorization", "Bearer " + accessToken);
    }

    //클라이언트 쿠키에서 삭제 (MaxAge = 0)
    public void deleteRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    //로그인 상태 확인
    public boolean isLoggedIn(String accessToken) {
        // null 값 확인
        if(accessToken == null) return false;

        // 블랙 리스트 확인
        return !isTokenInBlackList(accessToken);
    }

    //토큰 블랙리스트인지 확인
    private boolean isTokenInBlackList(String accessToken){
        return tokenBlackListRepository.existsByAccessToken(accessToken);
    }

}
