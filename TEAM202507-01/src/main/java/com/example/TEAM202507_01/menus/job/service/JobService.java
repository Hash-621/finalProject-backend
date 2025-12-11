package com.example.TEAM202507_01.menus.job.service;

import com.example.TEAM202507_01.menus.job.dto.JobPostDto;
import com.example.TEAM202507_01.menus.job.dto.JobUserPostDto;
import java.util.List;

public interface JobService {
    List<JobPostDto> findAllJobPosts();
    JobPostDto findJobPostById(Long id);
    void saveJobPost(JobPostDto jobPostDto);

    List<JobUserPostDto> findAllJobUserPosts();
    JobUserPostDto findJobUserPostById(Long id);
    void saveJobUserPost(JobUserPostDto jobUserPostDto);
}