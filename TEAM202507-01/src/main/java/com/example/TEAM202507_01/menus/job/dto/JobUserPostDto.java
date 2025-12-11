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
public class JobUserPostDto {
    private Long id;                // ★ 수정됨: String -> Long
    private String title;
    private Long companyId;
    private String userId;
    private String description;
    private String careerLevel;
    private String education;
    private LocalDate deadline;
    private Integer isActive;
    private LocalDateTime createdAt;
}