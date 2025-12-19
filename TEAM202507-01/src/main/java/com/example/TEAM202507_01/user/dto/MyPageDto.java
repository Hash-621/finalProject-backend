package com.example.TEAM202507_01.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyPageDto {
    // 회원 정보 수정을 위한 필드
    private String id;          // USERS.ID (PK, UUID)
    private String loginId;     // USERS.LOGIN_ID
    private String password;    // 변경할 비밀번호
    private String email;
    private String name;
    private String nickname;
    private String birthDate;   // USERS.BIRTHDATE
    private String gender;      // USERS.SEX

    // 목록 조회를 위한 공통 필드 (작성글, 댓글, 즐겨찾기 등)
    private Long targetId;      // 게시글ID, 댓글ID, 즐겨찾기ID 등
    private String title;       // 제목 (게시글 제목, 즐겨찾기 대상 제목)
    private String content;     // 내용 (댓글 내용)
    private String category;    // 카테고리 (FOV_LIST.CATEGORY 등)
    private String createdAt;   // 작성일
}