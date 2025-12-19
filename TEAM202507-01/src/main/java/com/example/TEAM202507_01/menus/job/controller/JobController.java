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

    @GetMapping("/crawl")
    public ResponseEntity<List<JobDto>> getJobs( // 👈 JobPostDto -> JobDto
                                                 @RequestParam(value = "keyword", required = false) String keyword,
                                                 @RequestParam(value = "career", required = false) String career,
                                                 @RequestParam(value = "education", required = false) String education
    ) {
        return ResponseEntity.ok(jobService.findAllJobPosts(keyword, career, education));
    }
}