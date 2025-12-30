package com.example.TEAM202507_01.config.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor  // ★ 이 어노테이션이 있어야 'new TokenDto()' 사용 가능
@AllArgsConstructor
public class TokenDto {
    private String token;
    private String grantType;       // 예: "Bearer"
    private String accessToken;     // 액세스 토큰
    private Long tokenExpiresIn;    // 만료 시간
    private String refreshToken;
}
