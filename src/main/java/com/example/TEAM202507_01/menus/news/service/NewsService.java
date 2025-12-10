package com.example.TEAM202507_01.menus.news.service;

import com.example.TEAM202507_01.menus.news.entity.News;
import java.util.List;

public interface NewsService {

    // 뉴스 목록 조회
    List<News> findAll();

    // 뉴스 상세 조회
    News findById(Long id);

    // 뉴스 등록 및 수정
    News save(News news);

    // 뉴스 삭제
    void delete(Long id);
}