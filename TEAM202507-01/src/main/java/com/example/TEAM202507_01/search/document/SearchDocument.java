package com.example.TEAM202507_01.search.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "integrated_search")
@Getter
@Setter
public class SearchDocument {

    @Id
    private String id; // ES용 ID (예: "NEWS-105")

    @Field(type = FieldType.Long)
    private Long originalId; // Oracle 실제 PK (예: 105)

    @Field(type = FieldType.Keyword)
    private String category; // "NEWS", "RESTAURANT"

    @Field(type = FieldType.Text, analyzer = "nori")
    private String title;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String content;
}
