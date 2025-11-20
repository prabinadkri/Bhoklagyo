package com.example.Bhoklagyo.mapper;

import com.example.Bhoklagyo.dto.MenuItemRequest;
import com.example.Bhoklagyo.dto.MenuItemResponse;
import com.example.Bhoklagyo.entity.MenuItem;
import com.example.Bhoklagyo.entity.Restaurant;
import org.springframework.stereotype.Component;

@Component
public class MenuItemMapper {
    
    public MenuItem toEntity(MenuItemRequest request, Restaurant restaurant) {
        MenuItem menuItem = new MenuItem();
        menuItem.setName(request.getName());
        menuItem.setDesc(request.getDesc());
        menuItem.setPrice(request.getPrice());
        menuItem.setRestaurant(restaurant);
        return menuItem;
    }
    
    public MenuItemResponse toResponse(MenuItem menuItem) {
        Long restaurantId = menuItem.getRestaurant() != null ? menuItem.getRestaurant().getId() : null;
        return new MenuItemResponse(
            menuItem.getId(),
            menuItem.getName(),
            menuItem.getDesc(),
            menuItem.getPrice(),
            restaurantId
        );
    }
}
