package com.example.TEAM202507_01.menus.news.service;

import com.example.TEAM202507_01.menus.news.dto.NewsDto;
import com.example.TEAM202507_01.menus.news.repository.NewsMapper;
import com.example.TEAM202507_01.search.document.SearchDocument;
import com.example.TEAM202507_01.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NewsServiceImpl implements NewsService {

    private final NewsMapper newsMapper;
    private final SearchService searchService; // ★ 검색 서비스 주입

    @Override
    @Transactional(readOnly = true)
    public List<NewsDto> findAll() {
        return newsMapper.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public NewsDto findById(Long id) {
        NewsDto news = newsMapper.findById(id);
        if (news == null) {
            throw new RuntimeException("해당 뉴스를 찾을 수 없습니다. ID: " + id);
        }
        return news;
    }

    @Override
    public void save(NewsDto news) {
        // 1. DB 저장
        if (news.getId() == null) {
            newsMapper.save(news);
        } else {
            newsMapper.update(news);
        }
    }

    @Override
    public void delete(Long id) {
        newsMapper.delete(id);
        // (선택) 삭제 시 검색엔진 데이터 삭제 로직 추가 가능
    }
}