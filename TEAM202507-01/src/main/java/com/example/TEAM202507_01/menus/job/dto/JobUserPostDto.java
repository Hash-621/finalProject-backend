package com.example.TEAM202507_01.menus.job.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobUserPostDto { // 👈 클래스 이름 확인하세요! (Dto)
    private Long id;
    private String category;
    private String userId;
    private String title;
    private String companyName;
    private String companyType;
    private String description;
    private String careerLevel;
    private String education;

    // 프론트엔드와 주고받을 때는 String이 가장 안전합니다.
    private String deadline;
    private String createdAt;

    private int isActive;
}