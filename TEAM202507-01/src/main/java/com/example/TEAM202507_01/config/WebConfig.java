package com.example.TEAM202507_01.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 모든 주소(/**)에 대해
        registry.addMapping("/**")
                // 프론트엔드 주소 허용 (포트 번호 정확히 확인!)
                .allowedOrigins("http://localhost:3000", "http://192.168.0.96:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true);
    }
}