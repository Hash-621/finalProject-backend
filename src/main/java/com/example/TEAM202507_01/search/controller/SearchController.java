package com.example.TEAM202507_01.search.controller;

import com.example.TEAM202507_01.search.Documents.SearchDocument;
import com.example.TEAM202507_01.search.repository.SearchRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

// 수정 필요
//public List<SearchDocument> searchIntegrated(String keyword) {
//    // ES에서 검색 수행
//    return SearchRepository.findByTitleOrContent(keyword, keyword);
//}

// 커뮤니티 검색
@GetMapping("/api/v1/community")
public ResponseEntity<List<SearchDocument>> search(@RequestParam String query) {
//
//    List<SearchDocument> result= CommunityService.searchIntegrated(query);
//    return ResponseEntity.ok(result);
    return ResponseEntity.ok(null);

}