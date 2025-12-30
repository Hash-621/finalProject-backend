package com.example.TEAM202507_01.menus.job.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobUserPostDto {
    private Long id; // 글 번호
    private String category; // 카테고리
    private String userId; // 작성자 ID (누가 썼는지)
    private String title; // 글 제목 ("열정 있는 개발자입니다")
    private String companyName; // (구직자 입장에선 불필요할 수 있으나 DB 구조 맞춤용)
    private String companyType; // 희망 기업 형태 등
    private String description; // 자기소개 내용
    private String careerLevel; // 내 경력
    private String education;// 내 학력

    // 프론트엔드와 주고받을 때는 String이 가장 안전합니다.
    private String deadline; // 구직 마감일
    private String createdAt; // 작성일

    private int isActive; // 구직 상태 (1: 구직중)
}