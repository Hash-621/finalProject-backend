package com.example.TEAM202507_01.user.controller;

import com.example.TEAM202507_01.config.jwt.TokenDto;
import com.example.TEAM202507_01.user.service.KakaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class KakaoController {

    private final KakaoService kakaoService;

    // 카카오 개발자 센터에 등록한 Redirect URI와 일치해야 함
    // http://localhost:8080/api/v1/auth/kakao/callback
    @GetMapping("/kakao/callback")
    public ResponseEntity<TokenDto> kakaoCallback(@RequestParam String code) {

        // 서비스에서 지지고 볶고 해서 JWT 토큰을 받아옴
        String jwtToken = kakaoService.kakaoLogin(code);

        // 클라이언트에게 JWT 전달
        return ResponseEntity.ok(TokenDto.builder()
                .token(jwtToken)
                .build());
    }
}