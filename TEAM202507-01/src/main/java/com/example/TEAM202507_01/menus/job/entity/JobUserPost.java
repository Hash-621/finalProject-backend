package com.example.TEAM202507_01.menus.job.entity;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobUserPost {
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