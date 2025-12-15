package com.example.TEAM202507_01.search.service;

import com.example.TEAM202507_01.search.document.SearchDocument;
import java.util.List;

public interface SearchService {
    void saveDocument(SearchDocument document); // 데이터 저장용
    List<SearchDocument> searchIntegrated(String keyword); // 검색용
}