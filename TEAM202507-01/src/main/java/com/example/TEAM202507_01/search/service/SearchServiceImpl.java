package com.example.TEAM202507_01.search.service;

import com.example.TEAM202507_01.search.document.SearchDocument;
import com.example.TEAM202507_01.search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchServiceImpl implements SearchService {

    // [수정 1] Repository를 사용하려면 필드로 선언해야 합니다. (final 필수)
    private final SearchRepository searchRepository;

    @Override
    public List<SearchDocument> searchIntegrated(String keyword) {
        // [수정 2] 'SearchRepository'(대문자, 인터페이스)가 아니라
        //         'searchRepository'(소문자, 변수)를 사용해야 합니다.
        return searchRepository.findByTitleOrContent(keyword, keyword);
    }
}