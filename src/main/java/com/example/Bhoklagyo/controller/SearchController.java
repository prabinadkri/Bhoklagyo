package com.example.Bhoklagyo.controller;

import com.example.Bhoklagyo.dto.PaginatedSearchResultResponse;
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
    public ResponseEntity<PaginatedSearchResultResponse> search(
            @RequestParam String keyword,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "20") Integer limit) {
        PaginatedSearchResultResponse results = searchService.searchPaginated(keyword, cursor, limit);
        return ResponseEntity.ok(results);
    }
}
