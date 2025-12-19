package com.example.TEAM202507_01.search.service;

import com.example.TEAM202507_01.search.document.SearchDocument;
import com.example.TEAM202507_01.search.dto.SearchDto;

import java.util.List;

public interface SearchService {
    SearchDto searchIntegrated(String keyword); // 검색용


    String migrateAllData();

    String restaurantDtoToEs();
    String tourDtoToEs();
    String tourPostDtoToEs();
    String newsDtoToEs();
    String jobUserPostDtoToEs();
    String jobDtoToEs();
    String hospitalDtoToEs();
    String communityPostDtoToEs();
    // TODO SearchService 구현하기
}