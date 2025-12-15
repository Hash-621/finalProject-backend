package com.example.TEAM202507_01.menus.job.repository;

import com.example.TEAM202507_01.menus.job.dto.JobDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface JobMapper {

    // 1. 전체 조회
    List<JobDto> findAll();

    // 2. 상세 조회
    JobDto findById(Long id);

    // 3. 등록 (Insert)
    void save(JobDto job);

    // 4. 수정 (Update)
    void update(JobDto job);

    // 5. 삭제 (Delete)
    void delete(Long id);
}