package com.example.TEAM202507_01.config.jwt;

import com.example.TEAM202507_01.config.exception.ErrorDetails;
// 🔥 [수정 1] CustomUserDetailsService 임포트
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // 추가
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails; // 추가
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    private final TokenProvider tokenProvider;
    // 🔥 [수정 2] DB 조회를 위해 서비스 주입 (Step 2에서 만든 서비스)
    private final CustomUserDetailsService customUserDetailsService;

    public static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String token = extractTokenFromRequestHeader(httpServletRequest);
        try {
            if (StringUtils.hasText(token) && tokenProvider.isValidToken(token)) {

                // --- 🔥 [수정 3] 기존 코드 주석 처리 후 변경 ---
                // 기존: Authentication authentication = tokenProvider.getAuthentication(token);

                // 변경: 1. 토큰에서 사용자 아이디(String)만 꺼냄 (메서드명 확인 필요: getUserId or getUsername)
                String userId = tokenProvider.getLoginId(token);

                // 변경: 2. DB에서 진짜 정보(PK 포함)를 가져옴
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId);

                // 변경: 3. 인증 객체 수동 생성
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                // ---------------------------------------------

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            // (에러 처리 코드는 그대로 두시면 됩니다)
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

    private String extractTokenFromRequestHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        return StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ") ? bearerToken.substring(7) : null;
    }

    private String resolveToken(HttpServletRequest request) {
        String token = null;

        // 1. 쿠키에서 먼저 찾기 (우선순위)
        if (request.getCookies() != null) {
            token = Arrays.stream(request.getCookies())
                    .filter(c -> "jwt_cookie".equals(c.getName())) // ★ 쿠키 이름 확인! (로그인 때 저장한 이름)
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);
        }

        // 2. 쿠키에 없으면 헤더에서 찾기 (기존 방식 Fallback)
        if (!StringUtils.hasText(token)) {
            String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
            if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
                token = bearerToken.substring(7);
            }
        }

        return token;
    }

}