package com.example.TEAM202507_01.menus.restaurant.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

// [어노테이션 설명]
// @Data: Getter, Setter, ToString, EqualsAndHashCode 등을 한 번에 만들어주는 롬복의 만능 어노테이션임.
// @Builder: 객체를 생성할 때 `RestaurantDto.builder().name("식당").build()` 처럼 직관적으로 만들 수 있게 해줌.
// @NoArgsConstructor: 파라미터 없는 기본 생성자를 만듦. JSON 파싱이나 JPA 사용 시 필수임.
// @AllArgsConstructor: 모든 필드를 파라미터로 받는 생성자를 만듦.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDto {
    // [어노테이션 설명]
// @Data: Getter, Setter, ToString, EqualsAndHashCode 등을 한 번에 만들어주는 롬복의 만능 어노테이션임.
// @Builder: 객체를 생성할 때 `RestaurantDto.builder().name("식당").build()` 처럼 직관적으로 만들 수 있게 해줌.
// @NoArgsConstructor: 파라미터 없는 기본 생성자를 만듦. JSON 파싱이나 JPA 사용 시 필수임.
// @AllArgsConstructor: 모든 필드를 파라미터로 받는 생성자를 만듦.

    // 1. 공통 카테고리
    // @JsonAlias("CATEGORY"): 외부 API에서 들어오는 JSON 키값이 "CATEGORY"일 때, 이 필드(category)에 자동으로 넣어달라는 뜻임.
    // 외부 데이터의 이름이 내 변수명과 다를 때 매핑해주기 위해 사용함.
    @JsonAlias("CATEGORY")
    private String category;

    // 2. 식당 기본 정보
    // 외부 API의 "REST_ID" 값을 id 변수에 매핑함.
    @JsonAlias("REST_ID")
    private Long id;                // 식당 고유 번호 (PK)

    @JsonAlias("REST_NM")
    private String name;            // 식당 이름

    @JsonAlias("ADDR")
    private String address;         // 주소

    @JsonAlias("DADDR")
    private String addressDetail;   // 상세 주소

    @JsonAlias("TELNO")
    private String phone;           // 전화번호

    @JsonAlias("OPEN_HR_INFO")
    private String openTime;        // 영업 시간

    @JsonAlias("TOB_INFO")
    private String restCategory;    // 식당 분류 (예: 한식, 중식)

    @JsonAlias("RPRS_MENU_NM")
    private String bestMenu;        // 대표 메뉴

    // List<String>: 메뉴가 여러 개일 수 있으므로 리스트로 받음.
    @JsonAlias("MENU_KORN_NM")
    private List<String> menu;      // 전체 메뉴 리스트

    @JsonAlias("MENU_KORN_ADD_INFO")
    private List<String> menuDetail; // 메뉴 상세 정보

    @JsonAlias("MENU_AMT")
    private List<String> price;     // 가격 정보

    @JsonAlias("SD_URL")
    private String url;             // 식당 관련 외부 링크 (네이버 지도 등)

    // 이 필드는 외부 API에서 오는 게 아니라, 우리가 크롤링해서 저장할 이미지 경로임.
    @JsonAlias("REST_IMAGE")
    private String imagePath;       // 이미지 파일 경로
}
