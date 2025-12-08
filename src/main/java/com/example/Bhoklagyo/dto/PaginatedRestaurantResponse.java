package com.example.Bhoklagyo.dto;

import java.util.List;

public class PaginatedRestaurantResponse {
    private List<RestaurantResponse> restaurants;
    private Long nextCursor;
    private boolean hasMore;

    public PaginatedRestaurantResponse() {}

    public PaginatedRestaurantResponse(List<RestaurantResponse> restaurants, Long nextCursor, boolean hasMore) {
        this.restaurants = restaurants;
        this.nextCursor = nextCursor;
        this.hasMore = hasMore;
    }

    public List<RestaurantResponse> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(List<RestaurantResponse> restaurants) {
        this.restaurants = restaurants;
    }

    public Long getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(Long nextCursor) {
        this.nextCursor = nextCursor;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }
}
