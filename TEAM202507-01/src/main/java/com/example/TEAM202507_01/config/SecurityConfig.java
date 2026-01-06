package com.example.TEAM202507_01.config;

import com.example.TEAM202507_01.config.jwt.JwtFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer; // ★ 추가된 import
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // 📝 로그 출력을 위한 로거 생성
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final JwtFilter jwtFilter;

    // ★★★ [핵심 추가] 정적 리소스(이미지)는 보안 필터를 거치지 않고 프리패스! ★★★
    // 이 설정이 있어야 WebMvcConfig의 리소스 핸들러가 정상 작동하여 이미지를 보여줍니다.
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/images/**", "/css/**", "/js/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                // 🔥 [추가 1] CORS 설정 적용
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 1. URL 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 커뮤니티, 채용정보 등 API 허용
                        .requestMatchers("/api/v1/community/**", "/api/v1/job/**").permitAll()
                        .requestMatchers("/api/v1/admin/visit").permitAll()
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        // 나머지 요청 허용
                        .anyRequest().permitAll()
                )

                // 4. [핵심] JwtFilter를 시큐리티 필터 체인에 끼워넣기!
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                // 🚀 [기존 코드] 에러 핸들링 (로그 출력)
                .exceptionHandling(error -> error
                        // 1. 인증 실패
                        .authenticationEntryPoint((request, response, authException) -> {
                            log.warn("🛑 [인증 실패 - 401] : {} || 원인: {}", request.getRequestURI(), authException.getMessage());
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        })
                        // 2. 인가 실패
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            log.warn("🚫 [권한 거부 - 403] : {} || 원인: {}", request.getRequestURI(), accessDeniedException.getMessage());
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
                        })
                );

        return http.build();
    }

    @Value("${server.address:localhost}") // 값이 없으면 localhost 기본값 사용
    String serveraddress;

    // 🔥 [추가 2] CORS 허용 설정 (프론트엔드 3000번 포트 접속 허용)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 프론트엔드 주소 허용
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://" + serveraddress + ":3000"));

        // GET, POST 등 모든 메소드 허용
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 모든 헤더 허용
        config.setAllowedHeaders(List.of("*"));

        // 쿠키/인증정보 포함 허용
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // 🔥 [기존 코드] AuthenticationManager 빈 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}