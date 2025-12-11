package com.example.TEAM202507_01.menus.restaurant.controller;

import com.example.TEAM202507_01.menus.restaurant.dto.RestaurantDto;
import com.example.TEAM202507_01.menus.restaurant.service.RestaurantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/restaurant")
public class RestaurantController {

    private final RestaurantService restaurantService;

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
}