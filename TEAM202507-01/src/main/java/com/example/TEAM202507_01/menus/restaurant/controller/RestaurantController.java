package com.example.TEAM202507_01.menus.restaurant.controller;

import com.example.TEAM202507_01.menus.restaurant.dto.RestaurantBlogDto;
import com.example.TEAM202507_01.menus.restaurant.dto.RestaurantDto;
import com.example.TEAM202507_01.menus.restaurant.service.RestaurantBlogService;
import com.example.TEAM202507_01.menus.restaurant.service.RestaurantCrawlerService;
import com.example.TEAM202507_01.menus.restaurant.service.RestaurantService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/restaurant")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final RestaurantCrawlerService crawlerService;
    private final RestaurantBlogService blogService;

    // 1. 목록 조회 (GET)
    @GetMapping
    public ResponseEntity<List<RestaurantDto>> getRestaurantList() { // 메서드명 수정
        return ResponseEntity.ok(restaurantService.findAll());
    }

    // 2. 상세 조회 (GET)
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDto> getRestaurantDetail(@PathVariable Long id) { // 메서드명 수정
        return ResponseEntity.ok(restaurantService.findById(id));
    }

    // 3. 등록 및 수정 (POST)
    @PostMapping
    public ResponseEntity<RestaurantDto> createRestaurant(@RequestBody RestaurantDto restaurant) { // Map -> DTO 변경
        return ResponseEntity.ok(restaurantService.save(restaurant));
    }

    // 4. 삭제 (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRestaurant(@PathVariable Long id) {
        restaurantService.delete(id);
        return ResponseEntity.ok("맛집 삭제 성공");
    }


    @PostMapping("/sync")
    public ResponseEntity<String> syncData() {
        String result = restaurantService.syncRestaurantData(); // 1~10페이지 수집 실행
        return ResponseEntity.ok(result);
    }

    @PostMapping("/images")
    public ResponseEntity<String> startCrawling() {
        crawlerService.crawlStoreImages();
        return ResponseEntity.ok("이미지 크롤링이 백그라운드에서 시작되었습니다. 로컬 폴더와 콘솔을 확인하세요.");
    }


    @GetMapping("/{id}/blogs")
    public ResponseEntity<List<RestaurantBlogDto.BlogItem>> getRestaurantBlogs(@PathVariable Long id) {
        List<RestaurantBlogDto.BlogItem> blogList = blogService.searchBlogList(id);
        return ResponseEntity.ok(blogList);
    }
}