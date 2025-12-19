package com.example.TEAM202507_01.search.document;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import java.util.List;

@Data
@Builder
@Document(indexName = "restaurant")
public class RestaurantDocument {

    @Id
    private Long id;

    // 1. 검색이 되어야 하는 필드들 (Text + nori 분석기)
    @Field(type = FieldType.Text, analyzer = "nori")
    private String name;            // 가게 이름 (검색 1순위)

    @Field(type = FieldType.Text, analyzer = "nori")
    private String address;         // 주소 (검색 2순위 "유성구 맛집")

    @Field(type = FieldType.Text, analyzer = "nori")
    private String bestMenu;        // 대표 메뉴

    @Field(type = FieldType.Text, analyzer = "nori")
    private List<String> menu;      // 🔥 [중요] 메뉴 리스트 (예: "짜장면" 검색 시 걸리게)

    @Field(type = FieldType.Text, analyzer = "nori")
    private List<String> menuDetail;      // 메뉴 상세설명도 검색하고 싶으면 Text

    @Field(type = FieldType.Text, analyzer = "nori")
    private String restCategory;    // '한식', '중식' (카테고리 필터용)

    // 2. 검색보단 '필터링'이나 '그냥 보여주기용' (Keyword)

    @Field(type = FieldType.Keyword)
    private String phone;           // 전화번호 (검색 안함, 보여주기용)

    @Field(type = FieldType.Keyword)
    private String openTime;        // 영업시간

    @Field(type = FieldType.Keyword)
    private List<String> price;     // 가격 (보여주기용)

    @Field(type = FieldType.Keyword)
    private String url;             // 네이버 지도 링크

    @Field(type = FieldType.Keyword)
    private String imagePath;       // 썸네일 이미지

    // addressDetail(상세주소)은 검색 가치가 낮아서 뺐지만, 필요하면 Keyword로 넣으세요.
}