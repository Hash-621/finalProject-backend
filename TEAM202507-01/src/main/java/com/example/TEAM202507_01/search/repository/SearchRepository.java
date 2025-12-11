package com.example.TEAM202507_01.search.repository;

import com.example.TEAM202507_01.search.document.SearchDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface SearchRepository extends ElasticsearchRepository<SearchDocument, String> {
    // 제목이나 내용에서 검색
    List<SearchDocument> findByTitleOrContent(String title, String content);
}
