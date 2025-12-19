package com.example.TEAM202507_01.menus.job.service;

import com.example.TEAM202507_01.menus.job.dto.JobDto;
import com.example.TEAM202507_01.menus.job.dto.JobUserPostDto;
import com.example.TEAM202507_01.menus.job.entity.JobPost;
import com.example.TEAM202507_01.menus.job.entity.JobUserPost;
import com.example.TEAM202507_01.menus.job.repository.JobMapper;
import com.example.TEAM202507_01.menus.job.repository.JobUserPostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class JobServiceImpl implements JobService {

    private final JobMapper jobMapper;
    private final JobUserPostMapper jobUserPostMapper;

    // ... (1. 기업 공고 부분은 생략 - 그대로 두세요) ...

    @Override
    @Transactional(readOnly = true)
    public List<JobDto> findAllJobPosts(String keyword, String career, String education) {
        return jobMapper.findAll(keyword, career, education).stream().map(this::convertToJobDto).collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public JobDto findJobPostById(Long id) {
        JobPost job = jobMapper.findById(id);
        if (job == null) throw new IllegalArgumentException("공고 없음");
        return convertToJobDto(job);
    }
    @Override
    public void saveJobPost(JobDto dto) { /* 기존 유지 */ }


    // =========================================================
    // 2. 사용자 구직 공고 (JobUserPost) - 🟢 수정 완료
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<JobUserPostDto> findAllJobUserPosts() {
        return jobUserPostMapper.findAll().stream()
                .map(this::convertToJobUserPostDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public JobUserPostDto findJobUserPostById(Long id) {
        JobUserPost post = jobUserPostMapper.findById(id);
        if (post == null) throw new IllegalArgumentException("게시물 없음");
        return convertToJobUserPostDto(post);
    }

    @Override
    public void saveJobUserPost(JobUserPostDto dto) {
        // 🟢 날짜 변환 없이 String 그대로 저장
        String safeDeadline = (dto.getDeadline() != null && !dto.getDeadline().isEmpty())
                ? dto.getDeadline() : null;

        JobUserPost post = JobUserPost.builder()
                .id(dto.getId())
                .category("JOBS")
                .userId(dto.getUserId())
                .title(dto.getTitle())
                .companyName(dto.getCompanyName())
                .companyType(dto.getCompanyType())
                .description(dto.getDescription())
                .careerLevel(dto.getCareerLevel())
                .education(dto.getEducation())
                .deadline(safeDeadline) // String 그대로
                .isActive(dto.getIsActive())
                .build();

        if (post.getId() == null) {
            jobUserPostMapper.insertJobUserPost(post);
        } else {
            jobUserPostMapper.updateJobUserPost(post);
        }
    }

    // =========================================================
    // 변환 로직
    // =========================================================

    private JobDto convertToJobDto(JobPost job) {
        String safeLink = (job.getLink() == null || job.getLink().isEmpty()) ? "https://www.saramin.co.kr" : job.getLink();
        return JobDto.builder()
                .id(job.getId())
                .category(job.getCategory())
                .title(job.getTitle())
                .companyName(job.getCompanyName())
                .companyType(job.getCompanyType())
                .description(job.getDescription())
                .careerLevel(job.getCareerLevel())
                .education(job.getEducation())
                .deadline(job.getDeadline())
                .link(safeLink)
                .isActive(job.getIsActive())
                .build();
    }

    private JobUserPostDto convertToJobUserPostDto(JobUserPost post) {
        return JobUserPostDto.builder()
                .id(post.getId())
                .category(post.getCategory())
                .userId(post.getUserId())
                .title(post.getTitle())
                .companyName(post.getCompanyName())
                .companyType(post.getCompanyType())
                .description(post.getDescription())
                .careerLevel(post.getCareerLevel())
                .education(post.getEducation())
                .deadline(post.getDeadline()) // String 그대로
                .isActive(post.getIsActive())
                .build();
    }
}