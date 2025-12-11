package com.example.TEAM202507_01.config.jwt;

import com.example.TEAM202507_01.config.exception.ErrorDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean { // Spring Security 필터 체인에서 GenericFilterBean 을 사용하여 JWT 인증 필터를 추가.
    private final TokenProvider tokenProvider;
    public static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String token = extractTokenFromRequestHeader(httpServletRequest);
        try {
            if (StringUtils.hasText(token) && tokenProvider.isValidToken(token)) {
                Authentication authentication = tokenProvider.getAuthentication(token);
                // 유효한 토큰이라면 SecurityContextHolder 에 인증 정보 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            // Filter 는 사용자 정의 예외처리를 할 수 없음
            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json");
            ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), e.getMessage(), token);

            JavaTimeModule javaTimeModule = new JavaTimeModule();
            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS"))); // LocalDateTime.now() 직렬화 시 문제 해결
            String json = new ObjectMapper().registerModule(javaTimeModule).writeValueAsString(errorDetails);

            httpResponse.getWriter().write(json);
        }
    }

    // Request Header 에서 토큰 정보를 추출
    private String extractTokenFromRequestHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        return StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ") ? bearerToken.substring(7) : null;
    }
}