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
public class JobPostDto {
    private Long id;
    private String category;        // ★ 수정됨: Spring -> String
    private String title;
    private Long companyId;

    private String companyName;     // (선택)
    private String companyAddress;  // (선택)

    private String description;
    private String careerLevel;
    private String education;
    private LocalDate deadline;
    private Integer isActive;       // ★ 필드 존재 확인 (Integer 타입 권장)
    private LocalDateTime createdAt;
}