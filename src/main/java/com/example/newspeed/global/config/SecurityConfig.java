package com.example.newspeed.global.config;

import com.example.newspeed.auth.filter.JwtAuthenticationFilter;
import com.example.newspeed.auth.filter.WhiteListManager;
import com.example.newspeed.auth.jwt.JwtAuthenticationProvider;
import com.example.newspeed.auth.jwt.JwtTokenProvider;
import com.example.newspeed.auth.jwt.TokenExtractor;
import com.example.newspeed.global.error.FilterException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final WhiteListManager whiteListManager;
    private final TokenExtractor tokenExtractor;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider,whiteListManager,tokenExtractor,jwtAuthenticationProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) //보호 기능 삭제
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //세션 사용하지 않음
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll() //특정 경로는 접근 허용
                        .anyRequest().authenticated() // 나머지 요청은 인증 필요
                )
                // JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 보다 먼저 실행되도록 등록
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build(); //필터 객체 반환
    }

    // 로그인 등 인증 처리 시 필요한 AuthenticationManager 빈 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
