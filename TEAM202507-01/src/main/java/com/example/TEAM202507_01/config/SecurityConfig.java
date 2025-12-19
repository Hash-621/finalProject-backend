package com.example.TEAM202507_01.config;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 📝 로그 출력을 위한 로거 생성
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                // 1. URL 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 커뮤니티, 채용정보 등 API 허용
                        .requestMatchers("/api/v1/community/**", "/api/v1/job/**").permitAll()
                        // 정적 리소스 허용
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        // 나머지 요청 허용 (개발 편의상)
                        .anyRequest().permitAll()
                )

                // 🚀 [추가된 부분] 에러 핸들링 (로그 출력)
                .exceptionHandling(error -> error
                        // 1. 인증 실패 (로그인 안 함 / 토큰 만료) 시 로그 출력
                        .authenticationEntryPoint((request, response, authException) -> {
                            log.warn("🛑 [인증 실패 - 401] : {} || 원인: {}", request.getRequestURI(), authException.getMessage());
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        })
                        // 2. 인가 실패 (권한 부족) 시 로그 출력
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            log.warn("🚫 [권한 거부 - 403] : {} || 원인: {}", request.getRequestURI(), accessDeniedException.getMessage());
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
                        })
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}