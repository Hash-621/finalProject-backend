package com.example.TEAM202507_01.search.document;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@Document(indexName = "job")
public class JobDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String title;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String companyName;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String description; // 상세 모집 요강

    @Field(type = FieldType.Text, analyzer = "nori")
    private String companyType; // 기업 형태

    // 필터용

    @Field(type = FieldType.Text, analyzer = "nori")
    private String category; // 기업 형태

    @Field(type = FieldType.Keyword)
    private String careerLevel; // 경력/신입 필터

    @Field(type = FieldType.Keyword)
    private String education;

    @Field(type = FieldType.Keyword)
    private String deadline;

    @Field(type = FieldType.Keyword)
    private String link; // URL

    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis")
    private LocalDateTime createdAt; // URL

    @Field(type = FieldType.Integer)
    private int isActive;
}