package com.example.TEAM202507_01.menus.news.repository;

import com.example.TEAM202507_01.menus.news.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    // 필요한 경우 쿼리 메서드 추가 (예: 제목 검색)
    // List<News> findByTitleContaining(String keyword);
}