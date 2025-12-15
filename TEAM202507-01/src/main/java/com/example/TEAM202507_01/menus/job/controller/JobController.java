package com.example.TEAM202507_01.menus.job.controller;

import com.example.TEAM202507_01.menus.job.dto.JobPostDto;
import com.example.TEAM202507_01.menus.job.dto.JobUserPostDto;
import com.example.TEAM202507_01.menus.job.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/job")
public class JobController {

    private final JobService jobService;

    // 1. 구인 공고 목록 조회 (기업용)
    @GetMapping("/posts")
    public ResponseEntity<List<JobPostDto>> getJobPostList() {
        return ResponseEntity.ok(jobService.findAllJobPosts());
    }

    // 2. 구인 공고 상세 조회 (기업용)
    @GetMapping("/posts/{id}")
    public ResponseEntity<JobPostDto> getJobPostDetail(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.findJobPostById(id));
    }

    // 3. 구인 공고 등록 (기업용)
    @PostMapping("/posts")
    public ResponseEntity<String> createJobPost(@RequestBody JobPostDto jobPostDto) {
        jobService.saveJobPost(jobPostDto);
        return ResponseEntity.ok("구인 공고 등록 성공");
    }

    // 4. 사용자 구인 게시물 목록 조회
    @GetMapping("/user-posts")
    public ResponseEntity<List<JobUserPostDto>> getJobUserPostList() {
        return ResponseEntity.ok(jobService.findAllJobUserPosts());
    }

    // 5. 사용자 구인 게시물 상세 조회
    @GetMapping("/user-posts/{id}")
    public ResponseEntity<JobUserPostDto> getJobUserPostDetail(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.findJobUserPostById(id));
    }

    // 6. 사용자 구인 게시물 등록
    @PostMapping("/user-posts")
    public ResponseEntity<String> createJobUserPost(@RequestBody JobUserPostDto jobUserPostDto) {
        jobService.saveJobUserPost(jobUserPostDto);
        return ResponseEntity.ok("사용자 구인 게시물 등록 성공");
    }
}