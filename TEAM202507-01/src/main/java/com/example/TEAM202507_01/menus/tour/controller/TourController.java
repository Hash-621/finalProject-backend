package com.example.TEAM202507_01.menus.tour.controller;

import com.example.TEAM202507_01.menus.tour.dto.TourDto;
import com.example.TEAM202507_01.menus.tour.service.TourService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tour")
public class TourController {

    private final TourService tourService;

    // 1. 목록 조회 (GET)
    @GetMapping
    public ResponseEntity<List<TourDto>> getTourList() { // 메서드명 수정
        return ResponseEntity.ok(tourService.findAll());
    }

    // 2. 상세 조회 (GET)
    @GetMapping("/{id}")
    public ResponseEntity<TourDto> getTourDetail(@PathVariable Long id) { // 메서드명 수정
        return ResponseEntity.ok(tourService.findById(id));
    }

    // 3. 등록 및 수정 (POST)
    @PostMapping
    public ResponseEntity<TourDto> createTour(@RequestBody TourDto tour) { // Map -> DTO 변경
        return ResponseEntity.ok(tourService.save(tour));
    }

    // 4. 삭제 (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTour(@PathVariable Long id) { // 메서드명 수정
        tourService.delete(id);
        return ResponseEntity.ok("관광지 삭제 성공");
    }
}