package com.example.TEAM202507_01.menus.news.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NewsDto {
    private Long id;            // 뉴스 ID id
    private String title;       // 뉴스 제목 title
    private String content;     // 뉴스 본문 content
    private String imageUrl;     // 이미지링크 image_url
    private String source;   // 언론사 source
    private String author;     // 저자 author
    private LocalDateTime publishedAt; // 발행시간 published_at
}
