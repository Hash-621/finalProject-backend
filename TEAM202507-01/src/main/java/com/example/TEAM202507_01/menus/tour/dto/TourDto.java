package com.example.TEAM202507_01.menus.tour.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TourDto {
    private Long id;            // 관광지 ID tourspotZip
    private String name;        // 관광지명 tourspotNm
    private String address;     // 주소 tourspotAddr
    private String strAddress;     // 도로명 주소 tourspotDtlAddr
    private String phone;       // 전화번호 refadNo
    private String openTime;    // 운영시간 mngTime
    private String price;       // 가격 tourUtlzAmt
    private String parking; // 주차유무 pkgFclt
    private String guide;    // 시설 부가 설명 cnvenFcltGuid
    private String url;    // URL urlAddr
    private String description; // 설명 tourspotSumm
}










