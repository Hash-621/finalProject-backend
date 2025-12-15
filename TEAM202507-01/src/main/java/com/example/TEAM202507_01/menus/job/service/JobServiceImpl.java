package com.example.TEAM202507_01.menus.job.service;

import com.example.TEAM202507_01.menus.job.dto.JobDto;
import com.example.TEAM202507_01.menus.job.dto.JobPostDto;
import com.example.TEAM202507_01.menus.job.dto.JobUserPostDto;
import com.example.TEAM202507_01.menus.job.repository.JobMapper;
import com.example.TEAM202507_01.menus.job.repository.JobUserPostMapper;
import com.example.TEAM202507_01.search.document.SearchDocument;
import com.example.TEAM202507_01.search.service.SearchService;
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
    private final SearchService searchService; // ★ 검색 서비스 주입

    // ================== 1. 기업 채용 공고 (JobPost) ==================

    @Override
    @Transactional(readOnly = true)
    public List<JobPostDto> findAllJobPosts() {
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

        // 1. DB 저장
        if (job.getId() == null) {
            jobMapper.save(job);
        } else {
            jobMapper.update(job);
        }

        // 2. ★ 엘라스틱서치 동기화
        try {
            SearchDocument doc = SearchDocument.builder()
                    .id("JOB_" + job.getId())
                    .originalId(job.getId())
                    .category("JOB")
                    .title(job.getTitle())
                    .content(job.getDescription()) // 채용 상세 내용 검색
                    .url("/jobs/" + job.getId()) // 상세 페이지 URL
                    .build();
            searchService.saveDocument(doc);
        } catch (Exception e) {
            System.err.println("구인공고 검색 등록 실패: " + e.getMessage());
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

        // 1. DB 저장
        if (post.getId() == null) {
            jobUserPostMapper.save(post);
        } else {
            jobUserPostMapper.update(post);
        }

        // 2. ★ 엘라스틱서치 동기화 (구직글도 검색되게 하고 싶다면)
        try {
            SearchDocument doc = SearchDocument.builder()
                    .id("JOB_USER_" + post.getId())
                    .originalId(post.getId())
                    .category("JOB_USER")
                    .title(post.getTitle())
                    .content(post.getDescription())
                    .url("/jobs/user/" + post.getId())
                    .build();
            searchService.saveDocument(doc);
        } catch (Exception e) {
            System.err.println("구직공고 검색 등록 실패: " + e.getMessage());
        }
    }

    // ... (변환 메서드는 기존과 동일하므로 생략하거나 기존 코드 유지) ...
    private JobPostDto convertToJobPostDto(JobDto job) {
        return JobPostDto.builder()
                .id(job.getId())
                .category(job.getCategory())
                .title(job.getTitle())
                .companyId(job.getCompanyId())
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