package com.example.TEAM202507_01.menus.community.controller;

import com.example.TEAM202507_01.menus.community.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController // 1. JSON 데이터를 반환하는 컨트롤러임을 명시
@RequiredArgsConstructor // 2. final 필드에 대해 생성자 주입 자동 생성 (Service 주입)
@RequestMapping("/api/v1/community") // 3. 기본 URL 경로 설정 (예: /api/v1/community)
public class CommunityController {

    private final CommunityService communityService;

    // 1. 목록 조회 (GET)
    @GetMapping
    public ResponseEntity<?> getCommunityList() {
        // 서비스에게 목록 가져오라고 시킴
        // return ResponseEntity.ok(communityService.findAll());
        return ResponseEntity.ok("커뮤니티 목록 조회 성공");
    }

    // 2. 상세 조회 (GET)
    @GetMapping("/{id}")
    public ResponseEntity<?> getCommunityDetail(@PathVariable Long id) {
        // return ResponseEntity.ok(communityService.findById(id));
        return ResponseEntity.ok("커뮤니티 상세 조회 성공: " + id);
    }

    // 3. 등록 (POST)
    @PostMapping
    public ResponseEntity<?> createCommunity(@RequestBody Map<String, Object> params) {
        // communityService.save(params);
        return ResponseEntity.ok("커뮤니티 등록 성공");
    }

    // 4. 삭제 (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCommunity(@PathVariable Long id) {
        // communityService.delete(id);
        return ResponseEntity.ok("커뮤니티 삭제 성공");
    }
}