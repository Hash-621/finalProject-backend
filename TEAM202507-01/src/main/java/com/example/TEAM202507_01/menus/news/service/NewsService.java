package com.example.TEAM202507_01.menus.news.service;

import com.example.TEAM202507_01.menus.news.dto.NewsDto;
import java.util.List;

public interface NewsService {
    List<NewsDto> findAll();
    NewsDto findById(Long id);
    NewsDto save(NewsDto news);
    void delete(Long id);
}