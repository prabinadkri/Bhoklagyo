package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.MenuItemRequest;
import com.example.Bhoklagyo.dto.MenuItemResponse;

import java.util.List;

public interface MenuItemService {
    List<MenuItemResponse> getMenuItemsByRestaurantId(Long restaurantId);
    List<MenuItemResponse> updateMenuItemsForRestaurant(Long restaurantId, List<MenuItemRequest> menuItemRequests);
    void deleteMenuItem(Long restaurantId, Long menuItemId);
}
