package com.example.TEAM202507_01.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {
    private Long id;
    private String category; // "NEWS", "TOUR", "JOB" 등
    private String title;    // 제목
    private String content;  // 내용 (요약)
    private String link;     // 상세 페이지 URL (/news/1, /tour/5 등)
}