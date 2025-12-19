package com.example.TEAM202507_01.menus.restaurant.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDto {

    // 1. 공통 카테고리 (설계서상 'RESTOURANTS' 고정)
    @JsonAlias("CATEGORY")
    private String category;

    // 2. 식당 기본 정보 (설계서 물리명 매칭)
    @JsonAlias("REST_ID")
    private Long id;                // REST_ID (NUMBER)
    @JsonAlias("REST_NM")
    private String name;            // REST_NAME
    @JsonAlias("ADDR")
    private String address;         // REST_ADDRESS
    @JsonAlias("DADDR")
    private String addressDetail;   // REST_ADDRESS_DETAIL
    @JsonAlias("TELNO")
    private String phone;           // REST_PHONE
    @JsonAlias("OPEN_HR_INFO")
    private String openTime;        // REST_OPEN_TIME
    @JsonAlias("TOB_INFO")
    private String restCategory;    // REST_CATEGORY (식당 분류)
    @JsonAlias("RPRS_MENU_NM")
    private String bestMenu;        // REST_BEST_MENU
    @JsonAlias("MENU_KORN_NM")
    private List<String> menu;            // REST_MENU (메뉴 이름들)
    @JsonAlias("MENU_KORN_ADD_INFO")
    private List<String> menuDetail;      // REST_MENU_DETAIL
    @JsonAlias("MENU_AMT")
    private List<String> price;           // REST_PRICE
    @JsonAlias("SD_URL")
    private String url;            // REST_URL;

    @JsonAlias("REST_IMAGE")
    private String imagePath; // REST_IMAGE 컬럼 대응
}



