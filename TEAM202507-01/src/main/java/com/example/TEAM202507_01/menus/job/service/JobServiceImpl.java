package com.example.TEAM202507_01.menus.job.service;

import com.example.TEAM202507_01.menus.job.dto.JobPostDto;
import com.example.TEAM202507_01.menus.job.dto.JobUserPostDto;
import com.example.TEAM202507_01.menus.job.dto.JobDto;
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

    // ================== 1. 기업 채용 공고 (JobPost) ==================

    @Override
    @Transactional(readOnly = true)
    public List<JobPostDto> findAllJobPosts() {
        // Entity List -> DTO List 변환
        return jobMapper.findAll().stream()
                .map(this::convertToJobPostDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public JobPostDto findJobPostById(Long id) {
        JobDto job = jobMapper.findById(id);
        if (job == null) throw new RuntimeException("채용 공고를 찾을 수 없습니다.");
        return convertToJobPostDto(job);
    }

    @Override
    public void saveJobPost(JobPostDto dto) {
        // DTO -> Entity 변환
        JobDto job = JobDto.builder()
                .id(dto.getId())
                .category(dto.getCategory())
                .title(dto.getTitle())
                .companyId(dto.getCompanyId())
                .description(dto.getDescription())
                .careerLevel(dto.getCareerLevel())
                .education(dto.getEducation())
                .deadline(dto.getDeadline())
                .isActive(dto.getIsActive())
                .build();

        if (job.getId() == null) {
            jobMapper.save(job);
        } else {
            jobMapper.update(job);
        }
    }

    // ================== 2. 사용자 구직 공고 (JobUserPost) ==================

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
        JobUserPostDto post = jobUserPostMapper.findById(id);
        if (post == null) throw new RuntimeException("구인 게시물을 찾을 수 없습니다.");
        return convertToJobUserPostDto(post);
    }

    @Override
    public void saveJobUserPost(JobUserPostDto dto) {
        JobUserPostDto post = JobUserPostDto.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .companyId(dto.getCompanyId())
                .userId(dto.getUserId())
                .description(dto.getDescription())
                .careerLevel(dto.getCareerLevel())
                .education(dto.getEducation())
                .deadline(dto.getDeadline())
                .isActive(dto.getIsActive())
                .build();

        if (post.getId() == null) {
            jobUserPostMapper.save(post);
        } else {
            jobUserPostMapper.update(post);
        }
    }

    // ================== 변환 메서드 (Helper Methods) ==================

    private JobPostDto convertToJobPostDto(JobDto job) {
        return JobPostDto.builder()
                .id(job.getId())
                .category(job.getCategory())
                .title(job.getTitle())
                .companyId(job.getCompanyId())
                // companyName 등은 조인이 필요하지만 여기선 생략
                .description(job.getDescription())
                .careerLevel(job.getCareerLevel())
                .education(job.getEducation())
                .deadline(job.getDeadline())
                .isActive(job.getIsActive())
                .createdAt(job.getCreatedAt())
                .build();
    }

    private JobUserPostDto convertToJobUserPostDto(JobUserPostDto post) {
        return JobUserPostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .companyId(post.getCompanyId())
                .userId(post.getUserId())
                .description(post.getDescription())
                .careerLevel(post.getCareerLevel())
                .education(post.getEducation())
                .deadline(post.getDeadline())
                .isActive(post.getIsActive())
                .createdAt(post.getCreatedAt())
                .build();
    }
}