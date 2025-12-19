package com.example.TEAM202507_01.menus.hospital.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HospitalDto {
    private Long id; //병원 관리 번호 (고유번호)
    private String category; //병원의 규모나 종류
    private String name; //병원이름
    private String treatCategory; //진료과목(소분류)
    private String address; //병원 주소
    private String tel; // 병원 전화번호
    private LocalDateTime editDate; //정보 수정일

    private Double averageRating; //평점 평균(별점)
    private Integer reviewCount; // 리뷰 개수


}
