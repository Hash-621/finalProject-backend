package com.example.TEAM202507_01.config.security;

import com.example.TEAM202507_01.user.dto.UserDto;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter // 이걸 써야 나중에 controller에서 user.getId() 가능
public class CustomUserDetails implements UserDetails {

    private final UserDto userDto; // 우리의 진짜 유저 정보

    // 생성자
    public CustomUserDetails(UserDto userDto) {
        this.userDto = userDto;
    }

    // 🔥 [핵심] 우리가 필요한 ID를 꺼내는 메서드 (Controller에서 씀)
    public String getId() {
        return userDto.getId();
    }

    // --- 아래는 스프링 시큐리티 필수 오버라이드 메서드들 ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        // 일단 기본 권한 부여 (나중에 DB에 ROLE 컬럼이 있다면 그걸 넣으면 됨)
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return authorities;
    }

    @Override
    public String getPassword() {
        return userDto.getPassword(); // 비밀번호
    }

    @Override
    public String getUsername() {
        return userDto.getLoginId(); // 로그인 아이디 (변수명 확인: userId or email)
    }

    // 계정 만료/잠금 여부 (일단 다 true로 설정해서 통과시킴)
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}