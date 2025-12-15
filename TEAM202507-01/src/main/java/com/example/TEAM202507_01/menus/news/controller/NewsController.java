package com.example.TEAM202507_01.menus.news.controller;

import com.example.TEAM202507_01.menus.news.dto.NewsDto;
import com.example.TEAM202507_01.menus.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/news")
public class NewsController {

    private final NewsService newsService; // JobService 제거하고 NewsService만 사용

    // 1. 목록 조회
    @GetMapping
    public ResponseEntity<List<NewsDto>> getNewsList() { // 메서드명 수정 (getJobList -> getNewsList)
        return ResponseEntity.ok(newsService.findAll());
    }

    // 2. 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<NewsDto> getNewsDetail(@PathVariable Long id) { // 메서드명 수정
        return ResponseEntity.ok(newsService.findById(id));
    }

    // 3. 등록 및 수정
    @PostMapping
    public ResponseEntity<NewsDto> createNews(@RequestBody NewsDto news) { // Map -> News DTO 변경
        return ResponseEntity.ok(newsService.save(news));
    }

    // 4. 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNews(@PathVariable Long id) {
        newsService.delete(id);
        return ResponseEntity.ok("뉴스 삭제 성공");
    }
}