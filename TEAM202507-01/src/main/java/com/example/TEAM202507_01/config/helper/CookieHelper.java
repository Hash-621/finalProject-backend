package com.example.TEAM202507_01.config.helper;

import com.example.TEAM202507_01.config.jwt.JwtPropertySource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieHelper {
    private final JwtPropertySource jwtPropertySource;

    public String makeJwtCookie(String jwt) {
        return ResponseCookie
                .from(jwtPropertySource.getCookieName(), jwt)
                .httpOnly(jwtPropertySource.isEnabledHttpOnly())
                .secure(jwtPropertySource.isEnabledSecure())
                .path(jwtPropertySource.getPath())
                .maxAge(jwtPropertySource.getMaxAge())
                .build()
                .toString();
    }

    public void deleteJwtCookie(HttpHeaders httpHeaders) {
        String cookie = ResponseCookie
                .from(jwtPropertySource.getCookieName(), "")
                .path(jwtPropertySource.getPath())
                .maxAge(0) // maxAge 를 0 으로 처리하여 쿠키 만료 처리
                .build()
                .toString();
        httpHeaders.add("Set-Cookie", cookie);

    }
}
