package com.example.TEAM202507_01.menus.job.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPost {
    private Long id;
    private String category;
    private String title;
    private String companyName;
    private String companyType;
    private String description;
    private String careerLevel;
    private String education;

    // 🚨 [필수 확인] 무조건 String 이어야 합니다. (LocalDate X)
    private String deadline;
    private String link;

    private LocalDateTime createdAt;
    private int isActive;
}