package com.example.TEAM202507_01.search.document;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Builder
@Document(indexName = "tour")
public class TourDocument {

    @Id
    private Long id;

    // 검색용 (Text + nori)
    @Field(type = FieldType.Text, analyzer = "nori")
    private String name;        // 관광지명

    @Field(type = FieldType.Text, analyzer = "nori")
    private String address;     // 지번 주소

    @Field(type = FieldType.Text, analyzer = "nori")
    private String strAddress;  // 도로명 주소 (검색 보조)

    @Field(type = FieldType.Text, analyzer = "nori")
    private String description; // 설명 (검색 핵심)

    @Field(type = FieldType.Text, analyzer = "nori")
    private String guide;       // 시설 부가 설명 (예: "유모차 대여" 검색 시 걸리게)

    @Field(type = FieldType.Text, analyzer = "nori")
    private String parking;     // 주차 정보 (예: "무료 주차" 검색)

    // 보여주기/필터용 (Keyword)
    @Field(type = FieldType.Keyword)
    private String phone;

    @Field(type = FieldType.Keyword)
    private String openTime;

    @Field(type = FieldType.Keyword)
    private String price;

    @Field(type = FieldType.Keyword)
    private String url;
}