package com.example.TEAM202507_01.menus.news.controller;

import com.example.TEAM202507_01.menus.news.dto.NewsDto;
import com.example.TEAM202507_01.menus.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
// [어노테이션 설명]
// @RestController: 이 클래스가 REST API 컨트롤러임을 명시함. 결과값으로 JSON 데이터를 반환함.
// @RequiredArgsConstructor: final 필드(Service) 생성자를 자동 생성함.
// @RequestMapping("/api/v1/news"): 이 컨트롤러의 기본 URL 주소를 설정함. 모든 요청은 이 주소로 시작함.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/news")
public class NewsController {
    // 연결 포인트: 서비스 인터페이스를 주입받음.
    // 실제로는 NewsServiceImpl 객체가 들어옴.
    private final NewsService newsService; // JobService 제거하고 NewsService만 사용

    // 1. 목록 조회
    // GET /api/v1/news 요청이 오면 실행됨.
    @GetMapping
    public ResponseEntity<List<NewsDto>> getNewsList() { // 메서드명 수정 (getJobList -> getNewsList)
        return ResponseEntity.ok(newsService.findAll());
    }

    // 2. 상세 조회
    // GET /api/v1/news/{id} 요청이 오면 실행됨 (예: /api/v1/news/1).
    @GetMapping("/{id}")
    public ResponseEntity<NewsDto> getNewsDetail(@PathVariable Long id) { // 메서드명 수정
        return ResponseEntity.ok(newsService.findById(id));
    }

//    // 3. 등록 및 수정
//    @PostMapping
//    public ResponseEntity<NewsDto> createNews(@RequestBody NewsDto news) { // Map -> News DTO 변경
//        return ResponseEntity.ok(newsService.save(news));
//    }

    // 4. 삭제
    // DELETE /api/v1/news/{id} 요청이 오면 실행됨.
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNews(@PathVariable Long id) {
        newsService.delete(id); // 서비스에게 삭제 명령을 내림.
        return ResponseEntity.ok("뉴스 삭제 성공"); // 성공 메시지를 문자열로 반환함.
    }

    @GetMapping("/daejeon")
    public ResponseEntity<?> getDaejeonNews(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "") String query) {
        // 1. 한 번에 가져올 개수 설정
        int display = 8;

        // 2. 시작 위치(start) 계산 공식 적용
        int start = (page - 1) * display + 1;

        // Service 호출 (start 값을 넘겨줍니다)
        return ResponseEntity.ok(newsService.getDaejeonNews(query, display, start));
    }
}

/// 전체 구조 및 흐름 요약

// NewsDto: 데이터를 담아 나르는 객체 (Data Transfer Object). 뉴스 정보(제목, 내용 등)를 담는다.
// NewsController: 사용자의 요청(HTTP)을 가장 먼저 받는 곳. "뉴스 보여줘"라는 요청을 받으면 Service에게 일을 시킨다
//NewsService (Interface): 비즈니스 로직의 설계도. 컨트롤러와 구현체 사이의 연결 고리이다.
//NewsServiceImpl: NewsService의 실제 구현체. 비즈니스 로직(저장, 조회 등)을 처리하고 Mapper를 호출한다.
//NewsMapper (Interface): 데이터베이스(DB)와 대화하는 창구(DAO 역할). MyBatis가 이 인터페이스를 보고 실제 SQL을 실행한다.

//흐름:
//Client(요청) → NewsController → NewsService → NewsServiceImpl → NewsMapper → DB(MyBatis)
