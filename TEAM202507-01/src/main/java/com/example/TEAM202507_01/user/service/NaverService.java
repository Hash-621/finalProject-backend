package com.example.TEAM202507_01.user.service;

import com.example.TEAM202507_01.config.jwt.TokenDto;
import com.example.TEAM202507_01.config.jwt.TokenProvider;
import com.example.TEAM202507_01.user.dto.UserDto;
import com.example.TEAM202507_01.user.dto.naver.NaverDto;
import com.example.TEAM202507_01.user.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map; // ★ Map 임포트 필수
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverService {

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;
    private final UserMapper userMapper;
    private final TokenProvider tokenProvider;

    // 1. 네이버 액세스 토큰 받기 (Map 사용으로 변경)
    public String getAccessToken(String code, String state) {
        log.info("🔹 [NaverService] 1. 액세스 토큰 요청 시작");
        String requestUrl = "https://nid.naver.com/oauth2.0/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("state", state);

        try {
            // ★ 수정됨: NaverDto 대신 Map으로 받아서 직접 꺼냅니다.
            ResponseEntity<Map> response = restTemplate.postForEntity(requestUrl, params, Map.class);

            // 로그로 실제 응답 데이터 확인
            log.info("   > 네이버 API 응답 Body: {}", response.getBody());

            if (response.getBody() == null) {
                return null;
            }

            // "access_token" 키로 값을 직접 꺼냄 (이름 불일치 문제 해결)
            return (String) response.getBody().get("access_token");

        } catch (Exception e) {
            log.error("❌ [NaverService] 토큰 요청 중 실패", e);
            throw new RuntimeException("네이버 토큰 발급 실패");
        }
    }

    // 2. 로그인 처리 및 JWT 발급
    public TokenDto loginWithNaver(String accessToken) {
        // accessToken이 null이면 여기서 바로 예외 처리 (NullPointer 방지)
        if (accessToken == null) {
            log.error("❌ [NaverService] accessToken이 null입니다. 토큰 발급 실패.");
            throw new RuntimeException("Access Token is null");
        }

        log.info("🔹 [NaverService] 2. 사용자 정보 요청 시작 (Token: {}...)", accessToken.substring(0, 5));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<NaverDto.UserInfoResponse> response = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                entity,
                NaverDto.UserInfoResponse.class
        );

        NaverDto.UserInfoResponse.Response naverUser = response.getBody().getResponse();
        String loginId = "NAVER_" + naverUser.getId();
        log.info("   > 가져온 사용자 ID: {}", loginId);

        UserDto existingUser = userMapper.findByLoginId(loginId);

        if (existingUser == null) {
            log.info("   > 신규 회원입니다. 회원가입 진행...");
            UserDto newUser = new UserDto();
            newUser.setId(UUID.randomUUID().toString());
            newUser.setLoginId(loginId);
            newUser.setPassword(UUID.randomUUID().toString());
            newUser.setEmail(naverUser.getEmail());
            newUser.setName(naverUser.getName());
            newUser.setNickname(naverUser.getNickname() != null ? naverUser.getNickname() : naverUser.getName());

            userMapper.insertUser(newUser);
            userMapper.saveAuthority(newUser.getLoginId(), "ROLE_USER");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                loginId,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.createToken(authentication);

        TokenDto tokenDto = new TokenDto();
        tokenDto.setGrantType("Bearer");
        tokenDto.setAccessToken(jwt);
        tokenDto.setTokenExpiresIn(86400000L);

        return tokenDto;
    }
}