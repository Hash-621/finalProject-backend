package com.example.TEAM202507_01.menus.community.controller;

import com.example.TEAM202507_01.alramo.service.AlramoService;
import com.example.TEAM202507_01.menus.community.dto.CommentDto;
import com.example.TEAM202507_01.menus.community.dto.CommunityDto;
import com.example.TEAM202507_01.menus.community.service.CommunityService;
import com.example.TEAM202507_01.user.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;
    private final AlramoService alramoService;
    private final FavoriteService favoriteService;

    // ==========================================
    // 🚑 1. [긴급 패치] 레거시 경로 지원 (프론트 호환용)
    // ==========================================

    @PostMapping(value = "/free", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Long> createFreePostLegacy(
            @RequestPart("dto") CommunityDto dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        log.info("🚑 [Legacy Support] POST /free 요청 감지 - 처리 시작");
        dto.setCategory("FREE");
        long postId = communityService.savePost(dto, files);
        try { alramoService.sendNewPostNotification(dto.getTitle()); } catch (Exception e) {}
        return ResponseEntity.ok(postId);
    }

    @PostMapping(value = "/recommend", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Long> createRecommendPostLegacy(
            @RequestPart("dto") CommunityDto dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        dto.setCategory("RECOMMEND");
        long postId = communityService.savePost(dto, files);
        return ResponseEntity.ok(postId);
    }

    // ==========================================
    // 📢 2. 게시글 조회 (Read)
    // ==========================================

    @GetMapping("/free")
    public ResponseEntity<List<CommunityDto>> getFreeBoardList() {
        return ResponseEntity.ok(communityService.getPostList("FREE", 1, 100));
    }

    @GetMapping("/recommend")
    public ResponseEntity<List<CommunityDto>> getRecommendBoardList() {
        return ResponseEntity.ok(communityService.getPostList("RECOMMEND", 1, 100));
    }

    @GetMapping("/posts")
    public ResponseEntity<List<CommunityDto>> getPostList(
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(communityService.getPostList(category, page, size));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<CommunityDto>> getPostsByCategoryPath(
            @PathVariable String category,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(communityService.getPostList(category.toUpperCase(), page, size));
    }

    @GetMapping("/post/{id:[0-9]+}")
    public ResponseEntity<CommunityDto> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(communityService.findPostById(id));
    }

    @GetMapping("/free/{id:[0-9]+}")
    public ResponseEntity<CommunityDto> getFreePostDetail(@PathVariable Long id) {
        return ResponseEntity.ok(communityService.findPostById(id));
    }

    // ✨ [추가된 API] 특정 게시글에 업로드된 모든 파일 경로 목록 조회
    // 상세 페이지 캐러셀 구현을 위해 반드시 필요합니다.
    @GetMapping("/post/{id:[0-9]+}/files")
    public ResponseEntity<List<String>> getPostFiles(@PathVariable Long id) {
        log.info("🖼️ 게시글 {} 번의 파일 목록 조회 요청", id);
        return ResponseEntity.ok(communityService.getFilePathsByPostId(id));
    }

    @GetMapping("/user/{userId}/others")
    public ResponseEntity<List<CommunityDto>> getOtherPosts(
            @PathVariable String userId,
            @RequestParam("currentPostId") Long currentPostId
    ) {
        return ResponseEntity.ok(communityService.getOtherPostsByUser(userId, currentPostId));
    }

    // ==========================================
    // 📝 3. 게시글 작성/삭제/이미지 (통합)
    // ==========================================

    @PostMapping("/image-upload")
    public ResponseEntity<String> uploadEditorImage(@RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(communityService.uploadEditorImage(file));
    }

    @PostMapping(value = "/post", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Long> createPost(
            @RequestPart("dto") CommunityDto dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        long postId = communityService.savePost(dto, files);
        try { alramoService.sendNewPostNotification(dto.getTitle()); } catch (Exception e) {}
        return ResponseEntity.ok(postId);
    }

    @DeleteMapping("/post/{id:[0-9]+}")
    public ResponseEntity<String> deletePost(@PathVariable Long id) {
        communityService.deletePost(id);
        return ResponseEntity.ok("게시글이 삭제되었습니다.");
    }

    // ==========================================
    // 💬 4. 댓글 (Comment)
    // ==========================================

    @GetMapping("/comments/{postId:[0-9]+}")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(communityService.findCommentsByPostId(postId));
    }

    @PostMapping("/comments")
    public ResponseEntity<String> saveComment(@RequestBody CommentDto dto) {
        communityService.saveComment(dto);
        return ResponseEntity.ok("댓글이 등록되었습니다.");
    }

    @PostMapping("/comments/delete")
    public ResponseEntity<String> deleteComment(@RequestBody Map<String, Long> body) {
        Long id = body.get("id");
        if (id != null) communityService.deleteComment(id);
        return ResponseEntity.ok("댓글이 삭제되었습니다.");
    }

    // ==========================================
    // ❤️ 5. 좋아요 (Favorite)
    // ==========================================

    @PostMapping("/post/{id:[0-9]+}/like")
    public ResponseEntity<?> toggleLike(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user
    ) {
        if (user == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");
        favoriteService.toggleFavorite("COMMUNITY", user.getUsername(), id);
        return ResponseEntity.ok(Map.of("message", "좋아요 처리 완료"));
    }
}