package com.example.TEAM202507_01.config.helper;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class SecurityHelper {
    public static Optional<String> getLoggedId() {
        // JwtFilter 의 doFilter 메소드에서 Request 가 들어올 때, SecurityContext 에 저장했던 Authentication 객체를 불러옴
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return Optional.empty();
        }

        String loggedId = null;
        if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            loggedId = springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            loggedId = (String) authentication.getPrincipal();
        }

        return Optional.ofNullable(loggedId);
    }
}