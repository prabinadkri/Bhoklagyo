package com.example.Bhoklagyo.controller;

import com.example.Bhoklagyo.dto.SearchResultResponse;
import com.example.Bhoklagyo.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/search")
public class SearchController {
    
    private final SearchService searchService;
    
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }
    
    @GetMapping
    public ResponseEntity<SearchResultResponse> search(@RequestParam String keyword) {
        SearchResultResponse results = searchService.search(keyword);
        return ResponseEntity.ok(results);
    }
}
