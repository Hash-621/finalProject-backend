package com.example.TEAM202507_01.menus.job.entity;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobUserPost {
    // 위와 동일하게 DB 테이블 구조를 그대로 옮겨놓은 클래스임.
    // 날짜 관련 필드는 DB와의 호환성을 위해 String이나 LocalDateTime을 상황에 맞춰 사용함.
    private Long id;
    private String category;
    private String userId;
    private String title;
    private String companyName;
    private String companyType;
    private String description;
    private String careerLevel;
    private String education;

    // 🟢 DB에서 이미 문자열이거나, 자동 변환을 위해 String 사용
    private String deadline;
    private String createdAt;

    private int isActive;
}