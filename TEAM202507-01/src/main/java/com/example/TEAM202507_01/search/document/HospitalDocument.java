package com.example.TEAM202507_01.search.document;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Data
@Builder
@Document(indexName = "hospital")
public class HospitalDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String name;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String address;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String treatCategory; // 진료과목 (내과, 치과 등) - 중요!

    @Field(type = FieldType.Keyword)
    private String tel;

    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis")
    private LocalDateTime editDate;

    // 정렬을 위해 숫자 타입 사용
    @Field(type = FieldType.Double)
    private Double averageRating; // 별점

    @Field(type = FieldType.Integer)
    private Integer reviewCount;  // 리뷰 수
}