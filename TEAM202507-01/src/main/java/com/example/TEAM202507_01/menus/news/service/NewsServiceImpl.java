package com.example.TEAM202507_01.menus.news.service;

import com.example.TEAM202507_01.menus.news.dto.NewsDto;
import com.example.TEAM202507_01.menus.news.repository.NewsMapper; // Mapper Import
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NewsServiceImpl implements NewsService {

    private final NewsMapper newsMapper; // Repository -> Mapper 변경

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
    public NewsDto save(NewsDto news) {
        if (news.getId() == null) {
            newsMapper.save(news); // 신규 등록
        } else {
            newsMapper.update(news); // 수정
        }
        return news;
    }

    @Override
    public void delete(Long id) {
        newsMapper.delete(id);
    }
}