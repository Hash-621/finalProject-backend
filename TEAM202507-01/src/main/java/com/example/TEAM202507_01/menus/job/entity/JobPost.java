package com.example.TEAM202507_01.menus.job.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPost {

    //필드는 DTO와 거의 같음.
    //// 차이점: DTO는 화면에 보여주기 위한 것, Entity는 DB 저장을 위한 것.
    //deadline이 String인 이유: "상시채용", "채용시 마감" 같은 문자가 들어올 수 있어서 날짜 타입(Date)을 못 씀.
    private Long id; // 글 번호.
    private String category; // 'JOBS' (채용정보 카테고리).
    private String title; // 공고 제목.
    private String companyName; // 회사 이름.
    private String companyType; // 기업 형태 (중소, 중견 등).
    private String description; // 근무 지역 등의 설명.
    private String careerLevel; // 경력 (신입, 경력 등).
    private String education; // 학력 (대졸, 초대졸 등).

    private String deadline; // 마감일 (String으로 저장).
    private String link;

    private LocalDateTime createdAt;
    private int isActive;
}