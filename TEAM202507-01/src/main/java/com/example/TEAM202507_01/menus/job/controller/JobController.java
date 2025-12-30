package com.example.TEAM202507_01.menus.job.controller;

import com.example.TEAM202507_01.menus.job.dto.JobDto; // 👈 Import 변경
import com.example.TEAM202507_01.menus.job.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/job")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;
    // GET /api/v1/job/crawl 요청을 받음.
    // 검색 조건(keyword, career, education)을 쿼리 파라미터(?keyword=...)로 받음.
    @GetMapping("/crawl")
    public ResponseEntity<List<JobDto>> getJobs(
                                                 @RequestParam(value = "keyword", required = false) String keyword,
                                                 @RequestParam(value = "career", required = false) String career,
                                                 @RequestParam(value = "education", required = false) String education
    ) {
        // 서비스를 호출해서 결과를 받아 그대로 응답함.
        return ResponseEntity.ok(jobService.findAllJobPosts(keyword, career, education));
    }
}