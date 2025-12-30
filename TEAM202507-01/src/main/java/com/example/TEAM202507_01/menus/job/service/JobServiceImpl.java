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

    private final JobMapper jobMapper; // 크롤링 공고 DB 관리자.
    private final JobUserPostMapper jobUserPostMapper; // 사용자 공고 DB 관리자.



    @Override
    @Transactional(readOnly = true) // 읽기 전용으로 성능 최적화.
    public List<JobDto> findAllJobPosts(String keyword, String career, String education) {
        // 1. 매퍼에게 조건(키워드, 경력, 학력)을 주고 DB에서 데이터를 가져옴.
        return jobMapper.findAll(keyword, career, education)
                .stream()// 2. 리스트를 스트림(흐름)으로 바꿈.
                .map(this::convertToJobDto) // 3. 하나씩 꺼내서 DTO로 변환함.
                .collect(Collectors.toList()); // 4. 다시 리스트로 포장해서 반환.
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
                .map(this::convertToJobUserPostDto) // DTO 변환
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
// [방어 로직] 날짜가 null이면 null로, 있으면 그대로 씀.
        String safeDeadline = (dto.getDeadline() != null && !dto.getDeadline().isEmpty())
                ? dto.getDeadline() : null;

// DTO 내용을 Entity(JobUserPost)로 옮겨 담음 (빌더 패턴).
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
                .deadline(safeDeadline)  // 문자열 날짜 그대로 저장
                .isActive(dto.getIsActive())
                .build();

        // ID가 없으면 '새 글' -> INSERT
        if (post.getId() == null) {
            jobUserPostMapper.insertJobUserPost(post);
        } else { // ID가 있으면 '수정' -> UPDATE
            jobUserPostMapper.updateJobUserPost(post);
        }
    }

    // =========================================================
    // 변환 로직
    // =========================================================

    // DB 원본 데이터를 화면용 데이터로 바꾸는 내부 메서드임.
    private JobDto convertToJobDto(JobPost job) {
        // 링크가 없으면 사람인 메인으로 보내버림.
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
                .link(safeLink) // 안전한 링크 넣기.
                .isActive(job.getIsActive())
                .build();
    }
    // 사용자 공고용 변환 메서드
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