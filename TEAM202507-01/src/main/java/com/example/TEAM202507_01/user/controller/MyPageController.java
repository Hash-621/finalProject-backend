package com.example.TEAM202507_01.user.controller;

import com.example.TEAM202507_01.user.dto.MyPageDto;
import com.example.TEAM202507_01.user.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/mypage")
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService myPageService;

    private String getUserId(UserDetails userDetails) {
        return (userDetails != null) ? userDetails.getUsername() : "testUser";
    }

    @GetMapping("/info")
    public ResponseEntity<?> getMyInfo(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(myPageService.getMyInfo(getUserId(userDetails)));
    }

    @GetMapping("/posts")
    public ResponseEntity<?> getMyPosts(@AuthenticationPrincipal UserDetails userDetails, @RequestParam(defaultValue = "1") int page) {
        int size = 10;
        return ResponseEntity.ok(myPageService.getMyPosts(getUserId(userDetails), (page-1)*size, size));
    }

    @GetMapping("/comments")
    public ResponseEntity<?> getMyComments(@AuthenticationPrincipal UserDetails userDetails, @RequestParam(defaultValue = "1") int page) {
        int size = 10;
        return ResponseEntity.ok(myPageService.getMyComments(getUserId(userDetails), (page-1)*size, size));
    }

    // 🟢 즐겨찾기 조회 API (이게 없으면 404/NoResourceFound 에러 발생)
    @GetMapping("/favorites")
    public ResponseEntity<?> getMyFavorites(@AuthenticationPrincipal UserDetails userDetails, @RequestParam(defaultValue = "1") int page) {
        int size = 10;
        return ResponseEntity.ok(myPageService.getMyFavorites(getUserId(userDetails), (page-1)*size, size));
    }

    @PutMapping("/info")
    public ResponseEntity<String> updateMyInfo(@AuthenticationPrincipal UserDetails userDetails, @RequestBody MyPageDto dto) {
        dto.setId(getUserId(userDetails));
        myPageService.updateMyInfo(dto);
        return ResponseEntity.ok("수정완료");
    }

    @PutMapping("/post/{id}")
    public ResponseEntity<String> updatePost(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id, @RequestBody Map<String, String> body) {
        myPageService.updatePost(id, getUserId(userDetails), body.get("title"), body.get("content"));
        return ResponseEntity.ok("수정완료");
    }

    @PutMapping("/comment/{id}")
    public ResponseEntity<String> updateComment(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id, @RequestBody Map<String, String> body) {
        myPageService.updateComment(id, getUserId(userDetails), body.get("content"));
        return ResponseEntity.ok("수정완료");
    }

    @DeleteMapping("/post/{id}")
    public ResponseEntity<String> deletePost(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        myPageService.deletePost(id, getUserId(userDetails));
        return ResponseEntity.ok("삭제완료");
    }

    @DeleteMapping("/comment/{id}")
    public ResponseEntity<String> deleteComment(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        myPageService.deleteComment(id, getUserId(userDetails));
        return ResponseEntity.ok("삭제완료");
    }

    @DeleteMapping("/favorite/{id}")
    public ResponseEntity<String> deleteFavorite(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        myPageService.deleteFavorite(id, getUserId(userDetails));
        return ResponseEntity.ok("삭제완료");
    }
}