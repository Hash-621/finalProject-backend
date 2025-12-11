package com.example.TEAM202507_01.menus.news.repository;

import com.example.TEAM202507_01.menus.news.dto.NewsDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface NewsMapper {

    // 1. 전체 조회
    List<NewsDto> findAll();

    // 2. 상세 조회
    NewsDto findById(Long id);

    // 3. 등록 (Insert)
    void save(NewsDto news);

    // 4. 수정 (Update)
    void update(NewsDto news);

    // 5. 삭제 (Delete)
    void delete(Long id);
}