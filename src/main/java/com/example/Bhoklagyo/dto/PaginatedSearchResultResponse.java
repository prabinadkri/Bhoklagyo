package com.example.Bhoklagyo.dto;

public class PaginatedSearchResultResponse {
    private PaginatedRestaurantResponse restaurants;
    private MenuItemSearchResponse menuItems;

    public PaginatedSearchResultResponse() {}

    public PaginatedSearchResultResponse(PaginatedRestaurantResponse restaurants, MenuItemSearchResponse menuItems) {
        this.restaurants = restaurants;
        this.menuItems = menuItems;
    }

    public PaginatedRestaurantResponse getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(PaginatedRestaurantResponse restaurants) {
        this.restaurants = restaurants;
    }

    public MenuItemSearchResponse getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(MenuItemSearchResponse menuItems) {
        this.menuItems = menuItems;
    }

    public static class MenuItemSearchResponse {
        private java.util.List<MenuItemResponse> items;

        public MenuItemSearchResponse() {}

        public MenuItemSearchResponse(java.util.List<MenuItemResponse> items) {
            this.items = items;
        }

        public java.util.List<MenuItemResponse> getItems() {
            return items;
        }

        public void setItems(java.util.List<MenuItemResponse> items) {
            this.items = items;
        }
    }
}
