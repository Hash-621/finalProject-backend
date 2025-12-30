package com.example.TEAM202507_01.user.controller;

import com.example.TEAM202507_01.user.dto.MyPageDto;
import com.example.TEAM202507_01.user.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    private String getLoginId(UserDetails userDetails) {
        if (userDetails == null) {
            throw new RuntimeException("로그인 정보가 없습니다.");
        }
        return userDetails.getUsername(); // hansh 반환
    }

    @GetMapping("/info")
    public ResponseEntity<?> getMyInfo(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(myPageService.getMyInfo(getLoginId(userDetails)));
    }

    @PutMapping("/info")
    public ResponseEntity<String> updateMyInfo(@AuthenticationPrincipal UserDetails userDetails, @RequestBody MyPageDto dto) {
        dto.setId(getLoginId(userDetails));
        myPageService.updateMyInfo(dto);
        return ResponseEntity.ok("수정완료");
    }

    @GetMapping("/posts")
    public ResponseEntity<?> getMyPosts(@AuthenticationPrincipal UserDetails userDetails, @RequestParam(defaultValue = "1") int page) {
        int size = 10;
        return ResponseEntity.ok(myPageService.getMyPosts(getLoginId(userDetails), (page-1)*size, size));
    }

    @GetMapping("/comments")
    public ResponseEntity<?> getMyComments(@AuthenticationPrincipal UserDetails userDetails, @RequestParam(defaultValue = "1") int page) {
        int size = 10;
        return ResponseEntity.ok(myPageService.getMyComments(getLoginId(userDetails), (page-1)*size, size));
    }

    @GetMapping("/favorites")
    public ResponseEntity<?> getMyFavorites(@AuthenticationPrincipal UserDetails userDetails, @RequestParam(defaultValue = "1") int page) {
        int size = 10;
        return ResponseEntity.ok(myPageService.getMyFavorites(getLoginId(userDetails), (page-1)*size, size));
    }

    // ... 나머지 update/delete 메서드들 (이전 코드와 동일) ...
    @PutMapping("/post/{id}")
    public ResponseEntity<String> updatePost(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id, @RequestBody Map<String, String> body) {
        myPageService.updatePost(id, getLoginId(userDetails), body.get("title"), body.get("content"));
        return ResponseEntity.ok("수정완료");
    }

    @PutMapping("/comment/{id}")
    public ResponseEntity<String> updateComment(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id, @RequestBody Map<String, String> body) {
        myPageService.updateComment(id, getLoginId(userDetails), body.get("content"));
        return ResponseEntity.ok("수정완료");
    }

    @DeleteMapping("/post/{id}")
    public ResponseEntity<String> deletePost(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        myPageService.deletePost(id, getLoginId(userDetails));
        return ResponseEntity.ok("삭제완료");
    }

    @DeleteMapping("/comment/{id}")
    public ResponseEntity<String> deleteComment(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        myPageService.deleteComment(id, getLoginId(userDetails));
        return ResponseEntity.ok("삭제완료");
    }

    @DeleteMapping("/favorite/{id}")
    public ResponseEntity<String> deleteFavorite(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        myPageService.deleteFavorite(id, getLoginId(userDetails));
        return ResponseEntity.ok("삭제완료");
    }
}