package com.example.TEAM202507_01.menus.job.repository;

import com.example.TEAM202507_01.menus.job.entity.JobPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface JobMapper {
    // 검색 조건(keyword, career, education)을 받아서 공고 목록을 조회함
    // @Param: XML 파일에서 #{keyword} 처럼 쓸 수 있게 이름을 지정해 줌
    List<JobPost> findAll(@Param("keyword") String keyword,
                          @Param("career") String career,
                          @Param("education") String education);

    // 상세 조회
    JobPost findById(Long id);

    // 크롤링한 데이터 1건 저장
    void insertJobPost(JobPost jobPost);

    // 공고 수정
    void updateJobPost(JobPost jobPost);

    // [중복 방지용]
    // 회사 이름과 공고 제목이 같은 게 DB에 몇 개나 있는지 셈. 0보다 크면 이미 저장된 것임.
    int countByCompanyAndTitle(@Param("companyName") String companyName, @Param("title") String title);

    // 전체 개수 세기
    int countAll();
    List<JobPost> findAllSearch();
}