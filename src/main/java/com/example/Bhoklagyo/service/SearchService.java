package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.PaginatedSearchResultResponse;
import com.example.Bhoklagyo.dto.SearchResultResponse;

public interface SearchService {
    SearchResultResponse search(String keyword);
    PaginatedSearchResultResponse searchPaginated(String keyword, Long cursor, Integer limit);
}
