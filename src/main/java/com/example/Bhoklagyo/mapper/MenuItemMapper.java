package com.example.Bhoklagyo.mapper;

import com.example.Bhoklagyo.dto.MenuItemRequest;
import com.example.Bhoklagyo.dto.MenuItemResponse;
import com.example.Bhoklagyo.entity.MenuItem;
import com.example.Bhoklagyo.entity.RestaurantMenuItem;
import org.springframework.stereotype.Component;

@Component
public class MenuItemMapper {
    
    public MenuItem toEntity(MenuItemRequest request) {
        MenuItem menuItem = new MenuItem();
        menuItem.setName(request.getName());
        return menuItem;
    }
    
    public MenuItemResponse toResponse(RestaurantMenuItem restaurantMenuItem) {
        return new MenuItemResponse(
            restaurantMenuItem.getId(),
            restaurantMenuItem.getMenuItem().getId(),
            restaurantMenuItem.getMenuItem().getName(),
            restaurantMenuItem.getDescription(),
            restaurantMenuItem.getPrice(),
            restaurantMenuItem.getRestaurant().getId(),
            restaurantMenuItem.getAvailable()
        );
    }
}
