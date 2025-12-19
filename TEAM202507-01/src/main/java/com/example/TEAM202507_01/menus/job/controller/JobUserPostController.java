package com.example.TEAM202507_01.menus.job.controller;

import com.example.TEAM202507_01.menus.job.dto.JobUserPostDto;
import com.example.TEAM202507_01.menus.job.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/job/user") // 👈 여기가 핵심 주소입니다
public class JobUserPostController {

    private final JobService jobService;

    // 1. 인재 목록 조회 (GET /api/v1/job/user/list)
    @GetMapping("/list")
    public ResponseEntity<List<JobUserPostDto>> getUserJobs() {
        log.info("📡 [GET] 인재 목록 조회 요청");
        return ResponseEntity.ok(jobService.findAllJobUserPosts());
    }

    // 2. 인재 프로필 등록 (POST /api/v1/job/user/post)
    // 🚨 아까 404 에러 나던 곳이 바로 여기입니다!
    @PostMapping("/post")
    public ResponseEntity<?> saveUserJob(@RequestBody JobUserPostDto dto) {
        log.info("📝 [POST] 구직 프로필 등록 요청: {}", dto.getTitle());

        // 날짜 등이 비어있을 경우에 대한 방어 로직은 Service에서 처리됨
        jobService.saveJobUserPost(dto);

        return ResponseEntity.ok("등록 성공");
    }

    // 3. 상세 조회 (필요 시 추가)
    @GetMapping("/{id}")
    public ResponseEntity<JobUserPostDto> getUserJobDetail(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.findJobUserPostById(id));
    }
}