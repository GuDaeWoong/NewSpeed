package com.example.newspeed.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

//토큰 생성, 추출, 검증 담당
@Component
public class JwtTokenService {
    private Key key;

    // HS256 알고리즘용 키 자동 생성
    @PostConstruct
    protected void init() {
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    //토큰 생성
    public String createToken(Long userId, long validityInMilliseconds) {
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
}
