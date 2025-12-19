package com.example.TEAM202507_01.menus.job.repository;

import com.example.TEAM202507_01.menus.job.entity.JobUserPost;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface JobUserPostMapper {
    // 반환 타입을 Entity(JobUserPost)로 맞춤
    List<JobUserPost> findAll();

    JobUserPost findById(Long id);

    // 저장 및 수정 시 Entity 객체를 받음
    void insertJobUserPost(JobUserPost jobUserPost);
    void updateJobUserPost(JobUserPost jobUserPost);
}