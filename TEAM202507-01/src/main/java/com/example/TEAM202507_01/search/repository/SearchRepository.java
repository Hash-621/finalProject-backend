package com.example.TEAM202507_01.search.repository;

import com.example.TEAM202507_01.search.document.SearchDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;

public interface SearchRepository extends ElasticsearchRepository<SearchDocument, String> {
    // 제목이나 내용에 검색어가 포함된 것 찾기
    List<SearchDocument> findByTitleContainingOrContentContaining(String title, String content);
}