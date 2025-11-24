package com.example.Bhoklagyo.dto;

import java.util.List;

public class SearchResultResponse {
    private List<RestaurantResponse> restaurants;
    private List<MenuItemResponse> menuItems;

    public SearchResultResponse() {}

    public SearchResultResponse(List<RestaurantResponse> restaurants, List<MenuItemResponse> menuItems) {
        this.restaurants = restaurants;
        this.menuItems = menuItems;
    }

    public List<RestaurantResponse> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(List<RestaurantResponse> restaurants) {
        this.restaurants = restaurants;
    }

    public List<MenuItemResponse> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItemResponse> menuItems) {
        this.menuItems = menuItems;
    }
}
