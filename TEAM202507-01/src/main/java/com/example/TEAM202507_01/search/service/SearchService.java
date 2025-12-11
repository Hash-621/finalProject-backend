package com.example.TEAM202507_01.search.service;

import com.example.TEAM202507_01.search.document.SearchDocument;

import java.util.List;

public interface SearchService {
    List<SearchDocument> searchIntegrated(String keyword);
}