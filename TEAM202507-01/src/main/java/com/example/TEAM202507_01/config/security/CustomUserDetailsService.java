package com.example.TEAM202507_01.config.security;

import com.example.TEAM202507_01.user.dto.UserDto;
import com.example.TEAM202507_01.user.repository.UserMapper; // 본인의 UserMapper 경로 확인!
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. DB에서 유저 조회 (UserMapper에 findByUserId 메서드가 있어야 함)
        UserDto userDto = userMapper.findByLoginId(username);

        if (userDto == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
        }

        // 2. 찾은 유저를 CustomUserDetails로 포장해서 리턴
        return new CustomUserDetails(userDto);
    }
}