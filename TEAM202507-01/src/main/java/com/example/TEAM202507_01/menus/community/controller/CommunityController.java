package com.example.TEAM202507_01.menus.community.controller;

import com.example.TEAM202507_01.alramo.service.AlramoService;
import com.example.TEAM202507_01.menus.community.dto.CommentDto;
import com.example.TEAM202507_01.menus.community.dto.CommunityDto;
import com.example.TEAM202507_01.menus.community.service.CommunityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
        try {
            dto.setCategory("FREE");
            communityService.savePost(dto);

            // 알림 발송 (에러가 나도 글 작성은 성공처리하기 위해 try-catch 내부 혹은 외부 배치 고려)
            try {
                alramoService.sendNewPostNotification(dto.getTitle());
            } catch (Exception e) {
                log.error("⚠️ 알림 발송 실패 (글은 저장됨): {}", e.getMessage());
            }

            return ResponseEntity.ok("자유게시판 저장 완료");
        } catch (Exception e) {
            log.error("❌ 게시글 저장 실패: ", e);
            return ResponseEntity.badRequest().body("게시글 저장 실패: " + e.getMessage());
        }
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
        try {
            dto.setCategory("RECOMMEND");
            communityService.savePost(dto);
            return ResponseEntity.ok("추천게시판 저장 완료");
        } catch (Exception e) {
            log.error("❌ 게시글 저장 실패: ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ==========================================
    // 📝 3. 게시글 상세 조회 (공통)
    // ==========================================
    @GetMapping("/post/{id}")
    public ResponseEntity<CommunityDto> getPostDetail(@PathVariable Long id) {
        log.info("📡 상세조회 요청 ID: {}", id);
        return ResponseEntity.ok(communityService.findPostById(id));
    }

    @GetMapping("/free/{id}")
    public ResponseEntity<CommunityDto> getFreePostDetail(@PathVariable Long id) {
        return ResponseEntity.ok(communityService.findPostById(id));
    }

    // ==========================================
    // 💬 4. 댓글 (Comment) 관련 매핑 - 여기가 문제의 핵심
    // ==========================================

    // 댓글 조회
    @GetMapping("/comments/{postId}")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(communityService.findCommentsByPostId(postId));
    }

    // ★ [수정됨] 댓글 작성: 에러 로그를 상세하게 찍도록 수정
    @PostMapping("/comments")
    public ResponseEntity<?> saveComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CommentDto dto
    ) {
        try {
            // 1. 로그인 여부 확인
            if (userDetails == null) {
                log.warn("⛔ 로그인되지 않은 사용자의 댓글 작성 시도");
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }

            String loginId = userDetails.getUsername();
            log.info("💬 [댓글작성 시도] ID: {}, 내용: {}", loginId, dto.getContent());

            // 2. DTO에 로그인 ID 세팅
            dto.setUserId(loginId);

            // 3. 서비스 호출 (여기서 CleanBot, UUID변환, DB저장 다 함)
            communityService.saveComment(dto);

            return ResponseEntity.ok("댓글 등록 성공");

        } catch (RuntimeException e) {
            // CleanBot 등에서 의도적으로 발생시킨 예외 (예: "비속어 감지됨")
            log.warn("🚫 댓글 작성 거부 (비즈니스 로직): {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e) {
            // DB 에러, API 키 에러 등 예상치 못한 시스템 에러
            // ★ 여기가 중요합니다. 콘솔에 빨간색으로 에러 줄글이 좍 뜰겁니다.
            log.error("🔥 [심각] 댓글 저장 중 시스템 에러 발생: ", e);
            return ResponseEntity.badRequest().body("시스템 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 댓글 삭제
    @PostMapping("/comments/delete")
    public ResponseEntity<?> deleteComment(@RequestBody Map<String, Long> payload) {
        try {
            Long id = payload.get("id");
            communityService.deleteComment(id);
            return ResponseEntity.ok("댓글 삭제 성공");
        } catch (Exception e) {
            log.error("❌ 댓글 삭제 실패: ", e);
            return ResponseEntity.badRequest().body("삭제 실패");
        }
    }
}