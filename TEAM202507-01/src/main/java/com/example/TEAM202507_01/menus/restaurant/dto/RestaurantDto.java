package com.example.TEAM202507_01.menus.restaurant.dto;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RestaurantDto {
    private Long id;            // 맛집 ID REST_ID
    private String name;        // 가게명 REST_NM
    private String address;     // 주소 ADDR
    private String addressDetail; // 상세주소 DADDR
    private String phone;       // 전화번호 TELNO
    private String openTime;       // 오픈시간 OPEN_HR_INFO
    private String category;    // 업종 (한식, 중식 등) TOB_INFO
    private String bestMenu;    // 대표메뉴 RPRS_MENU_NM
    private List<String> menu;    // 메뉴 MENU_KORN_NM (배열)
    private List<String> menuDetail;    // 메뉴 상세설명 ENU_KORN_ADD_INFO (배열), 대부분 메뉴 이름과 동일함
    private List<String> price;    // 가격 MENU_AMT (배열)
    private String url;    // url 링크 SD_URL
}













