package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.PaginatedSearchResultResponse;
import com.example.Bhoklagyo.dto.SearchResultResponse;

public interface SearchService {
    SearchResultResponse search(String keyword);
    PaginatedSearchResultResponse searchPaginated(String keyword, Long cursor, Integer limit);
    PaginatedSearchResultResponse searchPaginatedByLocation(String keyword, Double latitude, Double longitude, Long cursorId, Integer limit);
    PaginatedSearchResultResponse searchPaginatedByRating(String keyword, Long cursor, Integer limit);
    PaginatedSearchResultResponse searchPaginatedByPrice(String keyword, Long cursor, Integer limit);
}
