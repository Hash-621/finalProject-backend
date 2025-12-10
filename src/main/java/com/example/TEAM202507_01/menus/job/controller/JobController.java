package com.example.TEAM202507_01.menus.job.controller;

import com.example.TEAM202507_01.menus.hospital.service.HospitalService;
import com.example.TEAM202507_01.menus.job.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RestController // 1. JSON 데이터를 반환하는 컨트롤러임을 명시
@RequiredArgsConstructor // 2. final 필드에 대해 생성자 주입 자동 생성 (Service 주입)
@RequestMapping("/api/v1/job") // 3. 기본 URL 경로 설정 (예: /api/v1/community)
public class JobController {

    private final JobService jobService;

    // 1. 목록 조회 (GET)
    @GetMapping
    public ResponseEntity<?> getJobList() {
        // 서비스에게 목록 가져오라고 시킴
        // return ResponseEntity.ok(jobService.findAll());
        return ResponseEntity.ok("구인구직 목록 조회 성공");
    }

    // 2. 상세 조회 (GET)
    @GetMapping("/{id}")
    public ResponseEntity<?> getJobDetail(@PathVariable Long id) {
        // return ResponseEntity.ok(jobService.findById(id));
        return ResponseEntity.ok("구인구직 상세 조회 성공: " + id);
    }

    // 3. 등록 (POST)
    @PostMapping
    public ResponseEntity<?> createJob(@RequestBody Map<String, Object> params) {
        // jobService.save(params);
        return ResponseEntity.ok("구인구직 등록 성공");
    }

    // 4. 삭제 (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id) {
        // jobService.delete(id);
        return ResponseEntity.ok("구인구직 삭제 성공");
    }
}
