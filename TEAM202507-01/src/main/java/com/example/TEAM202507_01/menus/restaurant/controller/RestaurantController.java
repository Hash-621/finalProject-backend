package com.example.TEAM202507_01.menus.restaurant.controller;

import com.example.TEAM202507_01.config.security.CustomUserDetails;
import com.example.TEAM202507_01.menus.restaurant.dto.RestaurantBlogDto;
import com.example.TEAM202507_01.menus.restaurant.dto.RestaurantDto;
import com.example.TEAM202507_01.menus.restaurant.service.RestaurantBlogService;
import com.example.TEAM202507_01.menus.restaurant.service.RestaurantCrawlerService;
import com.example.TEAM202507_01.menus.restaurant.service.RestaurantService;
import com.example.TEAM202507_01.user.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController // 결과값으로 JSON을 반환하는 컨트롤러임.
@RequiredArgsConstructor
@RequestMapping("/api/v1/restaurant") // 기본 주소 설정
public class RestaurantController {

    // 4가지 서비스(일반, 크롤러, 블로그, 즐겨찾기)를 모두 주입받음.
    private final RestaurantService restaurantService;
    private final RestaurantCrawlerService crawlerService;
    private final RestaurantBlogService blogService;
    private final FavoriteService favoriteService;

    // 1. 목록 조회 (GET /api/v1/restaurant)
    @GetMapping
    public ResponseEntity<List<RestaurantDto>> getRestaurantList() {
        // 서비스에게 전체 목록 달라 하고, 200 OK와 함께 반환.
        return ResponseEntity.ok(restaurantService.findAll());
    }

    // 2. 상세 조회 (GET /api/v1/restaurant/{id})
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDto> getRestaurantDetail(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.findById(id));
    }

    // 3. 등록 (POST /api/v1/restaurant)
    @PostMapping
    public ResponseEntity<RestaurantDto> createRestaurant(@RequestBody RestaurantDto restaurant) {
        // @RequestBody: 들어오는 JSON 데이터를 DTO로 변환해서 받음.
        return ResponseEntity.ok(restaurantService.save(restaurant));
    }

    // 4. 삭제 (DELETE /api/v1/restaurant/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRestaurant(@PathVariable Long id) {
        restaurantService.delete(id);
        return ResponseEntity.ok("맛집 삭제 성공");
    }

    // 5. 블로그 검색 (GET /api/v1/restaurant/{id}/blogs)
    @GetMapping("/{id}/blogs")
    public ResponseEntity<RestaurantBlogDto> getRestaurantBlogs(@PathVariable Long id) {
        // Service가 DTO를 반환하므로 그대로 리턴
        RestaurantBlogDto result = blogService.searchBlogList(id);
        return ResponseEntity.ok(result);
    }

    // 6. 즐겨찾기 토글 (POST /api/v1/restaurant/{id}/favorite)
    @PostMapping("/{id}/favorite")
    public ResponseEntity<String> restaurantFavorite(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails // 🔥 현재 로그인한 사용자 정보
    ) {
        // 로그인 안 했으면 401 에러 반환
        if (userDetails == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        String userId = userDetails.getId();
        // 즐겨찾기 서비스 호출 (식당 타입, 유저ID, 식당ID)
        favoriteService.toggleFavorite("RESTOURANTS", userId, id);

        return ResponseEntity.ok("즐겨찾기 처리가 완료되었습니다.");
    }

    // A. 데이터 동기화 API (관리자용)
    @PostMapping("/sync")
    public ResponseEntity<String> syncData() {
        // 대전시 API 긁어오는 작업 실행
        String result = crawlerService.syncRestaurantData();
        return ResponseEntity.ok(result);
    }

    // B. 이미지 크롤링 API (관리자용)
    @PostMapping("/images")
    public ResponseEntity<String> startCrawling() {
        // 이미지 수집 작업 실행 (비동기라 바로 응답 옴)
        crawlerService.crawlStoreImages();
        return ResponseEntity.ok("이미지 크롤링이 백그라운드에서 시작되었습니다...");
    }
}
