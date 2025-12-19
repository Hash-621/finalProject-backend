package com.example.TEAM202507_01.menus.community.controller;

import com.example.TEAM202507_01.alramo.service.AlramoService;
import com.example.TEAM202507_01.menus.community.dto.CommentDto;
import com.example.TEAM202507_01.menus.community.dto.CommunityDto;
import com.example.TEAM202507_01.menus.community.service.CommunityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/community")
public class CommunityController {

    private final CommunityService communityService;
    private final AlramoService alramoService;

    // ==========================================
    // 📢 1. 자유게시판 (Free Board)
    // ==========================================
    @GetMapping("/free")
    public ResponseEntity<List<CommunityDto>> getFreeBoardList() {
        log.info("📡 [GET] /api/v1/community/free 요청 발생");
        return ResponseEntity.ok(communityService.findPostsByCategory("FREE"));
    }

    @PostMapping("/free")
    public ResponseEntity<?> saveFreePost(@RequestBody CommunityDto dto) {
        log.info("📝 [POST] 자유게시판 글 작성 요청: {}", dto.getTitle());
        dto.setCategory("FREE");
        communityService.savePost(dto);
        alramoService.sendNewPostNotification(dto.getTitle());
        return ResponseEntity.ok("자유게시판 저장 완료");
    }

    // ==========================================
    // 👍 2. 추천게시판 (Recommend Board)
    // ==========================================
    @GetMapping("/recommend")
    public ResponseEntity<List<CommunityDto>> getRecommendBoardList() {
        log.info("📡 [GET] /api/v1/community/recommend 요청 발생");
        return ResponseEntity.ok(communityService.findPostsByCategory("RECOMMEND"));
    }

    @PostMapping("/recommend")
    public ResponseEntity<?> saveRecommendPost(@RequestBody CommunityDto dto) {
        log.info("📝 [POST] 추천게시판 글 작성 요청: {}", dto.getTitle());
        dto.setCategory("RECOMMEND");
        communityService.savePost(dto);
        return ResponseEntity.ok("추천게시판 저장 완료");
    }

    // ==========================================
    // 📝 3. 게시글 상세 조회 (공통)
    // ==========================================
    @GetMapping("/post/{id}")
    public ResponseEntity<CommunityDto> getPostDetail(@PathVariable Long id) {
        log.info("📡 상세조회 요청 ID: {}", id);
        return ResponseEntity.ok(communityService.findPostById(id));
    }

    // (구버전 호환용 - 필요 없다면 삭제 가능)
    @GetMapping("/free/{id}")
    public ResponseEntity<CommunityDto> getFreePostDetail(@PathVariable Long id) {
        return ResponseEntity.ok(communityService.findPostById(id));
    }

    // ==========================================
    // 💬 4. 댓글 (Comment) 관련 매핑
    // ==========================================

    // 댓글 조회
    @GetMapping("/comments/{postId}")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(communityService.findCommentsByPostId(postId));
    }

    // 댓글 작성
    @PostMapping("/comments")
    public ResponseEntity<?> saveComment(@RequestBody CommentDto dto) {
        try {
            communityService.saveComment(dto);
            return ResponseEntity.ok("댓글 등록 성공");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 댓글 삭제
    @PostMapping("/comments/delete")
    public ResponseEntity<?> deleteComment(@RequestBody Map<String, Long> payload) {
        Long id = payload.get("id");
        communityService.deleteComment(id);
        return ResponseEntity.ok("댓글 삭제 성공");
    }
}