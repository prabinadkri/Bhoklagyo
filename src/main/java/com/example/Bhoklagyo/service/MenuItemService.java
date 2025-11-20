package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.MenuItemRequest;
import com.example.Bhoklagyo.dto.MenuItemResponse;

import java.util.List;

public interface MenuItemService {
    List<MenuItemResponse> getMenuItemsByRestaurantId(Long restaurantId);
    List<MenuItemResponse> addMenuItemsToRestaurant(Long restaurantId, List<MenuItemRequest> menuItemRequests);
    MenuItemResponse updateRestaurantMenuItem(Long restaurantMenuItemId, MenuItemRequest menuItemRequest);
    void deleteRestaurantMenuItem(Long restaurantMenuItemId);
}
