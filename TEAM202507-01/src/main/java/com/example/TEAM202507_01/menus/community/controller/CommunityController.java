package com.example.TEAM202507_01.menus.community.controller;

import com.example.TEAM202507_01.menus.community.dto.CommunityDto;
import com.example.TEAM202507_01.menus.community.dto.PostDto;
import com.example.TEAM202507_01.menus.community.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // 1. JSON 데이터를 반환하는 컨트롤러
@RequiredArgsConstructor // 2. 서비스(Service) 자동 주입
@RequestMapping("/api/v1/community") // 3. 기본 URL 경로
public class CommunityController {

    private final CommunityService communityService;

    // 1. 목록 조회
    @GetMapping
    public ResponseEntity<List<CommunityDto>> getAllCommunityList() {
        return ResponseEntity.ok(communityService.findAll());
    }

    // 2. 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<CommunityDto> getCommunityDetail(@PathVariable Long id) {
        return ResponseEntity.ok(communityService.findById(id));
    }

    // 3. 등록 (POST)
    @PostMapping
    public ResponseEntity<CommunityDto> createCommunity(@RequestBody PostDto postDto) {
        // DTO -> Entity 변환
        CommunityDto community = CommunityDto.builder()
                .id(postDto.getId())
                .userId(postDto.getUserId())    // ★ 작성자 ID 추가 (DB 저장 시 필수)
                .category(postDto.getCategory())
                .title(postDto.getTitle())
                .content(postDto.getContent())  // ★ 수정됨: 제목이 아니라 내용을 넣어야 함
                .viewCount(0L)                  // 조회수 초기화
                .build();

        CommunityDto savedCommunity = communityService.save(community);


        return ResponseEntity.ok(savedCommunity);
    }

    // 4. 삭제 (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCommunity(@PathVariable Long id) {
        communityService.delete(id);
        return ResponseEntity.ok("게시글 삭제 성공");
    }
}