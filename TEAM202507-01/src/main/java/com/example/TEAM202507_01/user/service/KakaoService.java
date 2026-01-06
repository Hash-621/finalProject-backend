package com.example.TEAM202507_01.user.service;

import com.example.TEAM202507_01.config.jwt.TokenProvider;
import com.example.TEAM202507_01.user.dto.CreateUserDto;
import com.example.TEAM202507_01.user.dto.UserDto;
import com.example.TEAM202507_01.user.dto.kakao.KakaoTokenResponse;
import com.example.TEAM202507_01.user.dto.kakao.KakaoUserInfo;
import com.example.TEAM202507_01.user.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoService {

    private final UserMapper userMapper;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.token-uri}")
    private String tokenUri;

    @Value("${kakao.user-info-uri}")
    private String userInfoUri;

    @Transactional
    public String kakaoLogin(String code) {
        // 1. "인가 코드"로 "카카오 액세스 토큰" 받기
        String kakaoAccessToken = getKakaoAccessToken(code);

        // 2. "카카오 액세스 토큰"으로 "사용자 정보" 가져오기
        KakaoUserInfo kakaoUserInfo = getKakaoUserInfo(kakaoAccessToken);

        // 3. 우리 DB에 있는지 확인하고, 없으면 회원가입 시키기
        String kakaoLoginId = "kakao_" + kakaoUserInfo.getId(); // 예: kakao_123456789
        UserDto userDto = registerKakaoUserIfNeed(kakaoLoginId, kakaoUserInfo);

        // 4. 강제 로그인 처리 (JWT 발급을 위해)
        Authentication authentication = forceLogin(userDto);

        // 5. 우리 서버 전용 JWT 토큰 발급 및 리턴
        return tokenProvider.createToken(authentication);
    }

    // --- 내부 메서드들 ---

    // 1. 토큰 요청
    private String getKakaoAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        // 🔥 [추가] 콘솔에 찍어서 확인해보세요! (범인 색출)
        System.out.println("============== 카카오 토큰 요청 정보 ==============");
        System.out.println("1. client_id: [" + clientId + "]");  // 여기에 공백 있는지, 이상한 값인지 확인
        System.out.println("2. redirect_uri: [" + redirectUri + "]"); // 192.168.0.101 인지 확인
        System.out.println("3. code: [" + code + "]");
        System.out.println("==============================================");

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<KakaoTokenResponse> response = restTemplate.exchange(
                tokenUri,
                HttpMethod.POST,
                kakaoTokenRequest,
                KakaoTokenResponse.class
        );

        return response.getBody().getAccessToken();
    }

    // 2. 유저 정보 요청
    private KakaoUserInfo getKakaoUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

        ResponseEntity<KakaoUserInfo> response = restTemplate.exchange(
                userInfoUri,
                HttpMethod.POST,
                kakaoProfileRequest,
                KakaoUserInfo.class
        );

        return response.getBody();
    }

    // 3. 회원가입/조회 로직
    // ... import 문에 CreateUserDTO 추가 ...

    private UserDto registerKakaoUserIfNeed(String loginId, KakaoUserInfo kakaoUserInfo) {
        // 1. DB에서 조회 (기존 로직 유지)
        UserDto existingUser = userMapper.findByLoginId(loginId);

        // 🔥 [디버깅] 카카오가 진짜 데이터를 주는지 콘솔에서 확인!
        System.out.println("====== 카카오 사용자 정보 수신 ======");
        System.out.println("닉네임: " + kakaoUserInfo.getKakaoAccount().getProfile().getNickname());
        System.out.println("이메일: " + kakaoUserInfo.getKakaoAccount().getEmail());
        System.out.println("=================================");

        if (existingUser != null) {
            return existingUser; // 이미 가입된 회원이면 바로 리턴
        }

        // 2. 없으면 회원가입 진행 -> 🔥 여기서 CreateUserDTO 사용!
        String nickname = kakaoUserInfo.getKakaoAccount().getProfile().getNickname();
        if (nickname == null) {
            nickname = "UnknownUser";
        }
        String randomPassword = UUID.randomUUID().toString();
        String encodedPassword = passwordEncoder.encode(randomPassword);
        String newUuid = UUID.randomUUID().toString();

        // 🔥 UserDto 대신 CreateUserDTO 생성
        CreateUserDto newUser = new CreateUserDto();
        newUser.setId(newUuid);
        newUser.setLoginId(loginId); // DTO 필드명 확인 (userId인지 loginId인지)
        newUser.setPassword(encodedPassword);
        newUser.setName(nickname); // 임시방편
        newUser.setNickname(nickname);
        newUser.setEmail(kakaoUserInfo.getKakaoAccount().getEmail());

        // 4. 🔥 [추가] 누락된 필드 기본값 처리 (이게 없으면 null로 들어감)
        newUser.setGender(null);       // 성별 모름 (Unknown)
        newUser.setBirthDate(null);   // 생년월일은 정보가 없으니 null (DB에서 허용해야 함)

        // 3. DB 저장 (Mapper가 CreateUserDTO를 받도록 수정 필요)
        userMapper.save(newUser);
        userMapper.saveAuthority(newUser.getLoginId(), "ROLE_USER");

        // 4. 🔥 [중요] forceLogin 메서드를 위해 UserDto로 변환해서 리턴
        // (CreateUserDTO에는 없는 필드(createdAt 등)가 있을 수 있으므로 변환 과정 필요)
        UserDto returnDto = new UserDto();
        returnDto.setId(newUser.getId());
        returnDto.setLoginId(newUser.getLoginId());
        returnDto.setPassword(newUser.getPassword());
        returnDto.setName(newUser.getName());
        returnDto.setNickname((newUser.getNickname()));
        returnDto.setEmail(newUser.getEmail());
        // returnDto.setRole("ROLE_USER"); // 필요하다면

        return returnDto;
    }

    // 4. 강제 로그인 (Authentication 객체 생성)
    private Authentication forceLogin(UserDto userDto) {
        // 비밀번호 검증 없이 강제로 Authentication 객체를 만듭니다.
        // authorities는 필요하다면 userDto에서 꺼내서 설정하세요.
        UserDetails principal = new User(userDto.getLoginId(), "", Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));

        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());

        // 시큐리티 컨텍스트에 저장 (선택사항, JWT만 리턴할거면 안해도 됨)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authentication;
    }
}