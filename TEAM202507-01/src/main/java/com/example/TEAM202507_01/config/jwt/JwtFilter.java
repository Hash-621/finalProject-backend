package com.example.TEAM202507_01.config.jwt;

import com.example.TEAM202507_01.config.exception.ErrorDetails;
import com.example.TEAM202507_01.config.security.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    private final TokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    public static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        // 1. 토큰 추출 (쿠키 -> 헤더 순서)
        String token = resolveToken(httpServletRequest);

        try {
            // 2. 토큰 유효성 검사
            if (StringUtils.hasText(token) && tokenProvider.isValidToken(token)) {

                // 3. 토큰에서 사용자 ID 추출
                String userId = tokenProvider.getLoginId(token);

                // 4. DB에서 사용자 상세 정보 로드
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId);

                // 5. 인증 객체 생성 및 Context 설정
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(servletRequest, servletResponse);

        } catch (Exception e) {
            // 에러 발생 시 JSON 응답 처리
            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json");

            ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), e.getMessage(), token);

            JavaTimeModule javaTimeModule = new JavaTimeModule();
            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS")));
            String json = new ObjectMapper().registerModule(javaTimeModule).writeValueAsString(errorDetails);

            httpResponse.getWriter().write(json);
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String token = null;

        // 1. 쿠키에서 먼저 찾기 (우선순위)
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                // ★ 중요: 로그에서 확인된 실제 쿠키 이름 "jwt_cookie" 사용
                if ("token".equals(c.getName())) {
                    token = c.getValue();
                    break; // 찾았으면 반복문 종료
                }
            }
        }

        // 2. 쿠키에 없으면 헤더에서 찾기 (Bearer 토큰)
        if (!StringUtils.hasText(token)) {
            String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
            if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
                token = bearerToken.substring(7);
            }
        }

        return token;
    }
}