package com.example.TEAM202507_01.security;

import com.example.TEAM202507_01.user.dto.UserDto;
import com.example.TEAM202507_01.user.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(final String loginId) {
        UserDto user = userMapper.findByLoginId(loginId);
        List<SimpleGrantedAuthority> grantedAuthorities = user.getAuthorities().stream().map(SimpleGrantedAuthority::new).toList();
        return new User(user.getLoginId(), user.getPassword(), grantedAuthorities);
    }
}