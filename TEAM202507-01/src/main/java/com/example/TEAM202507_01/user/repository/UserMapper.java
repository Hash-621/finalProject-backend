package com.example.TEAM202507_01.user.repository;

import com.example.TEAM202507_01.user.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {

    // 1. 전체 회원 조회 (관리자용)
    List<UserDto> findAll();

    // 2. 회원 상세 조회 (로그인 ID로 조회)
    UserDto findByLoginId(String loginId);

    // 🔥 [추가] 권한 저장 메서드
    // 파라미터로 아이디(loginId)와 권한명(authName)을 받습니다.
    void saveAuthority(@Param("loginId") String loginId, @Param("authName") String authName);

    // 4. 회원가입 (Insert)
    void save(CreateUserDto user);

    // 5. 회원 정보 수정 (Update)
    void update(UserDto user);

    // 6. 회원 탈퇴 (Delete)
    void delete(String loginId);

    // 7. 아이디 중복 체크
    int countByLoginId(String loginId);

    String findRostId(FindUserIdDto findUserIdDto);

    void insertUser(UserDto userDto);

    int resetPw(ResetPasswordDto resetPasswordDto);

    void updatePw(UpdatePwDto updatePwDto);

    int countByEmail(String email);

}