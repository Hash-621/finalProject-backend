package com.example.TEAM202507_01.search.controller;

import com.example.TEAM202507_01.search.document.SearchDocument;
import com.example.TEAM202507_01.search.dto.SearchDto;
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
    public ResponseEntity<SearchDto> search(@RequestParam String query) {
        return ResponseEntity.ok(searchService.searchIntegrated(query));
    }
    @PostMapping("/restaurantdata")
    public ResponseEntity<String> RestaurantDtoToData(){
        String result = searchService.restaurantDtoToEs();
        return ResponseEntity.ok(result);
    }
    @PostMapping("/searchdata")
    public ResponseEntity<String> allDtoToData() {
        String result = searchService.migrateAllData();
        return ResponseEntity.ok(result);
    }
}