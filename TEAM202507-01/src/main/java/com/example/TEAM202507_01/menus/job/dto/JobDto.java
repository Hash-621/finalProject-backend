package com.example.TEAM202507_01.menus.job.dto;

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
public class JobDto {
    private Long id;                // 게시물 ID
    private String category;        // 카테고리
    private String title;           // 제목
    private Long companyId;         // 회사 ID
    private String description;     // 내용
    private String careerLevel;     // 경력
    private String education;       // 학력
    private LocalDate deadline;     // 마감일
    private Integer isActive;       // 마감 여부 (SQL: IS_ACTIVATE)
    private LocalDateTime createdAt;// 작성일
}