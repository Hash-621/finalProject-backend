package com.example.TEAM202507_01.menus.job.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company {
    private Long id;        // PK (DB에서 자동 생성)
    private Long type;      // 기업 형태 (FK -> JOB_TYPE의 ID, 예: 1)
    private String name;    // 회사명
    private String address; // 주소
    private String phone;   // 전화번호
}