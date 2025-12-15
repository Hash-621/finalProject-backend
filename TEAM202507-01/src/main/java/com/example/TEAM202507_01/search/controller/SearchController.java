package com.example.TEAM202507_01.search.controller;

import com.example.TEAM202507_01.search.document.SearchDocument;
import com.example.TEAM202507_01.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<List<SearchDocument>> search(@RequestParam String query) {
        return ResponseEntity.ok(searchService.searchIntegrated(query));
    }
}