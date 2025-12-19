package com.example.TEAM202507_01.menus.job.repository;

import com.example.TEAM202507_01.menus.job.entity.JobPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface JobMapper {
    // 🔍 검색 조건 파라미터 추가
    List<JobPost> findAll(@Param("keyword") String keyword,
                          @Param("career") String career,
                          @Param("education") String education);

    JobPost findById(Long id);
    void insertJobPost(JobPost jobPost);
    void updateJobPost(JobPost jobPost);
    int countByCompanyAndTitle(@Param("companyName") String companyName, @Param("title") String title);
    int countAll();
    List<JobPost> findAllSearch();
}