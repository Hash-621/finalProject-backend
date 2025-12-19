package com.example.TEAM202507_01.menus.job.service;

import com.example.TEAM202507_01.menus.job.dto.JobDto; // 👈 Import 변경
import com.example.TEAM202507_01.menus.job.dto.JobUserPostDto;
import java.util.List;

public interface JobService {
    // 반환 타입 변경: List<JobPostDto> -> List<JobDto>
    List<JobDto> findAllJobPosts(String keyword, String career, String education);

    JobDto findJobPostById(Long id);
    void saveJobPost(JobDto dto);

    // 사용자 구직 공고 관련 (유지)
    List<JobUserPostDto> findAllJobUserPosts();
    JobUserPostDto findJobUserPostById(Long id);
    void saveJobUserPost(JobUserPostDto dto);
}