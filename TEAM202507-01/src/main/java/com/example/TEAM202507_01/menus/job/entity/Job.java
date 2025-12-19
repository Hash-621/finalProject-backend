package com.example.TEAM202507_01.menus.job.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Job {
    private Long id;            // PK
    private String category;    // 'JOBS'
    private String title;       // 제목

    // [변경] ID 대신 직접 정보 저장
    private String companyName; // 회사 이름
    private String companyType; // 회사 직종

    private String description; // 내용 (VARCHAR2 2000)
    private String careerLevel; // 경력
    private String education;   // 학력
    private String deadline; // 마감일
    private Integer isActive;   // 마감여부 (1:모집중, 0:마감)
    private LocalDateTime createdAt; // 생성일
}