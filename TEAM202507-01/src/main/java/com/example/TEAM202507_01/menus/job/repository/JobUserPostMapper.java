package com.example.TEAM202507_01.menus.job.repository;

import com.example.TEAM202507_01.menus.job.dto.JobUserPostDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface JobUserPostMapper {
    List<JobUserPostDto> findAll();
    JobUserPostDto findById(Long id);
    void save(JobUserPostDto post);
    void update(JobUserPostDto post);
    void delete(Long id);
}