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

    @GetMapping("/nearby")
    public ResponseEntity<PaginatedSearchResultResponse> searchNearby(
            @RequestParam String keyword,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false) Long offset,
            @RequestParam(defaultValue = "20") Integer limit) {
        PaginatedSearchResultResponse results = searchService.searchPaginatedByLocation(keyword, latitude, longitude, offset, limit);
        return ResponseEntity.ok(results);
    }
    @GetMapping("/rating")
    public ResponseEntity<PaginatedSearchResultResponse> searchRating(
            @RequestParam String keyword,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "20") Integer limit
    ) {
        PaginatedSearchResultResponse results =  searchService.searchPaginatedByRating(keyword,cursor,limit);
        return ResponseEntity.ok(results);
    }
    @GetMapping("/price")
    public ResponseEntity<PaginatedSearchResultResponse> searchPrice(
            @RequestParam String keyword,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "20") Integer limit
    ) {
        PaginatedSearchResultResponse results =  searchService.searchPaginatedByPrice(keyword,cursor,limit);
        return ResponseEntity.ok(results);
    }
}
