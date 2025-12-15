package com.example.TEAM202507_01.search.controller;

import com.example.TEAM202507_01.search.document.SearchDocument;
import com.example.TEAM202507_01.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search") // URL 경로를 /search로 통합
public class SearchController {

    private final SearchService searchService;

    // 통합 검색 API
    // 예시: GET /api/v1/search?query=대전
    @GetMapping
    public ResponseEntity<List<SearchDocument>> search(@RequestParam String query) {
        List<SearchDocument> result = searchService.searchIntegrated(query);
        return ResponseEntity.ok(result);
    }
}