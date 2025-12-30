package com.example.TEAM202507_01.menus.job.controller;

import com.example.TEAM202507_01.menus.job.dto.JobUserPostDto;
import com.example.TEAM202507_01.menus.job.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j // 로그 출력용.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/job/user")
public class JobUserPostController {

    private final JobService jobService;

    // 1. 인재 목록 조회 (GET /api/v1/job/user/list)
    @GetMapping("/list")
    public ResponseEntity<List<JobUserPostDto>> getUserJobs() {
        log.info("📡 [GET] 인재 목록 조회 요청");
        // 서비스에게 전체 목록 달라고 함.
        return ResponseEntity.ok(jobService.findAllJobUserPosts());
    }

    // 2. 인재 프로필 등록 (POST /api/v1/job/user/post)

    @PostMapping("/post")
    public ResponseEntity<?> saveUserJob(@RequestBody JobUserPostDto dto) {
        log.info("📝 [POST] 구직 프로필 등록 요청: {}", dto.getTitle());

        // @RequestBody: 프론트엔드가 보낸 JSON을 DTO 객체로 변환함
        jobService.saveJobUserPost(dto);

        return ResponseEntity.ok("등록 성공"); // 서비스에게 저장 시킴.
    }

    // 3. 상세 조회 (필요 시 추가)
    @GetMapping("/{id}")
    public ResponseEntity<JobUserPostDto> getUserJobDetail(@PathVariable Long id) {

        // ID로 하나만 찾아서 반환.
        return ResponseEntity.ok(jobService.findJobUserPostById(id));
    }
}

///전체 연결 구조 및 요약 ///

//흐름1: 채용 공고 (JobPost) - 외부 데이터 수집
//JobCrawlerService가 사람인 사이트를 돌면서 공고를 긁어옴.
//이때 수료일(2026.01.21) 조건을 체크하고, JobMapper를 통해 중복 검사 후 DB에 저장함.
//사용자가 웹에서 조회를 요청하면 JobController -> JobService -> JobMapper -> DB 순으로 데이터를 가져와 보여줌.

//흐름 2: 인재 정보 (JobUserPost) - 내부 사용자 작성
//사용자가 웹에서 "나 구직합니다" 글을 씀.
//JobUserPostController가 받아서 JobService로 넘김
//JobService는 데이터를 JobUserPostMapper에게 넘기고, DB에 저장됨.