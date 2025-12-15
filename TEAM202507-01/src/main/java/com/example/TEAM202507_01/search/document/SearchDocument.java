package com.example.TEAM202507_01.search.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// indexName = "테이블명" 같은 개념
@Document(indexName = "integrated_search")
public class SearchDocument {

    @Id
    private String id; // ES용 ID (예: "NEWS_1")

    @Field(type = FieldType.Keyword)
    private String category; // "NEWS", "JOB", "POST"

    @Field(type = FieldType.Long)
    private Long originalId; // 원본 DB의 PK

    @Field(type = FieldType.Text, analyzer = "nori") // 한글 형태소 분석기
    private String title;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String content;

    @Field(type = FieldType.Keyword)
    private String url; // 클릭 시 이동 경로
}