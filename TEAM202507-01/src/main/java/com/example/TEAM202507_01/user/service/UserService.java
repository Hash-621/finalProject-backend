package com.example.TEAM202507_01.user.service;

import com.example.TEAM202507_01.user.dto.CreateUserDto;
import com.example.TEAM202507_01.user.dto.UserDto;
import com.example.TEAM202507_01.user.dto.UserSignInDto;

import java.util.List;

public interface UserService {
    List<UserDto> findAll();
    UserDto findById(String loginId);
    String createToken(UserSignInDto signInDto);
    void join(CreateUserDto user); // 회원가입
    void update(UserDto user);
    void delete(String loginId);
}