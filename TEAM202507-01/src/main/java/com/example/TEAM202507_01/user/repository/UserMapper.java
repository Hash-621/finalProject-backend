package com.example.TEAM202507_01.user.repository;

import com.example.TEAM202507_01.user.dto.CreateUserDto;
import com.example.TEAM202507_01.user.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {

    // 1. 전체 회원 조회 (관리자용)
    List<UserDto> findAll();

    // 2. 회원 상세 조회 (로그인 ID로 조회)
    UserDto findByLoginId(String loginId);

    // 4. 회원가입 (Insert)
    void save(CreateUserDto user);

    // 5. 회원 정보 수정 (Update)
    void update(UserDto user);

    // 6. 회원 탈퇴 (Delete)
    void delete(String loginId);

    // 7. 아이디 중복 체크
    int countByLoginId(String loginId);
}