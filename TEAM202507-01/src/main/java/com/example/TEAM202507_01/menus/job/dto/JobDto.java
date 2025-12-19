package com.example.TEAM202507_01.menus.job.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobDto {
    private Long id;
    private String category;
    private String title;
    private String companyName;
    private String companyType;
    private String description;

    // DB 컬럼명: CAREER_LEVEL -> 프론트엔드: career
    private String careerLevel;

    private String education;
    private String deadline;

    // 🚨 핵심 유지: DB의 link 데이터를 프론트엔드에선 'url'로 인식하게 함
    @JsonProperty("url")
    private String link;

    private int isActive;

    // 프론트엔드 호환용 Getter (careerLevel을 career로 내보냄)
    public String getCareer() {
        return careerLevel;
    }
}