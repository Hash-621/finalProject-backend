package com.example.TEAM202507_01.search.repository;

import com.example.TEAM202507_01.search.document.SearchDocument;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface SearchMapper {
    // 통합 검색 (뉴스, 게시판, 구인구직 등에서 키워드로 검색)
    List<SearchDocument> searchIntegrated(String keyword);
}