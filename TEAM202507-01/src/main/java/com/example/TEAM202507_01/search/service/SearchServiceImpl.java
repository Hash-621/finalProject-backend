package com.example.TEAM202507_01.search.service;

import com.example.TEAM202507_01.search.document.SearchDocument;
import com.example.TEAM202507_01.search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final SearchRepository searchRepository;

    @Override
    public void saveDocument(SearchDocument document) {
        searchRepository.save(document);
    }

    @Override
    public List<SearchDocument> searchIntegrated(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        return searchRepository.findByTitleContainingOrContentContaining(keyword, keyword);
    }
}