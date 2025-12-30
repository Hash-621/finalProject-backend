package com.example.TEAM202507_01.menus.hospital.dto;

import lombok.Data;


@Data //@Data: 위와 동일하게 필수 메서드 자동 생성용임.
public class HospitalMapDto {
    private Long id; //병원 관리 번호 (고유번호)
    private String name; //병원이름
    private String treatCategory; //진료과목(소분류)
    private String address;  // 좌표 변환을 위한 주소.
    private String tel; // 병원 전화번호
}
