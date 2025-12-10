package com.example.TEAM202507_01.menus.news.service;

import com.example.TEAM202507_01.menus.news.entity.News;
import com.example.TEAM202507_01.menus.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;

    @Override
    public List<News> findAll() {
        return newsRepository.findAll();
    }

    @Override
    public News findById(Long id) {
        return newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 뉴스를 찾을 수 없습니다. ID: " + id));
    }

    @Override
    public News save(News news) {
        return newsRepository.save(news);
    }

    @Override
    public void delete(Long id) {
        if (!newsRepository.existsById(id)) {
            throw new RuntimeException("삭제하려는 뉴스가 존재하지 않습니다. ID: " + id);
        }
        newsRepository.deleteById(id);
    }
}