package com.example.TEAM202507_01.menus.hospital.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HospitalReviewDto {
    private Long Id; //리뷰의 고유 번호
    private Long hospitalId; //어느병원에 쓴 리뷰
    private String userId; //작성자의 고유 아이디
    private String userNickname; // 화면에 보여줄 작성자 닉네임
    private String content; // 리뷰내용
    private Integer rating; //별점
    private LocalDateTime createdAt; //작성일

}
