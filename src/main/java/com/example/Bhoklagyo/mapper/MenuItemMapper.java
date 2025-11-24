package com.example.Bhoklagyo.mapper;

import com.example.Bhoklagyo.dto.MenuItemRequest;
import com.example.Bhoklagyo.dto.MenuItemResponse;
import com.example.Bhoklagyo.entity.Category;
import com.example.Bhoklagyo.entity.RestaurantMenuItem;
import org.springframework.stereotype.Component;

@Component
public class MenuItemMapper {
    
    public Category toEntity(MenuItemRequest request) {
        Category category = new Category();
        category.setName(request.getCategoryName());
        return category;
    }
    
    public MenuItemResponse toResponse(RestaurantMenuItem restaurantMenuItem) {
        return new MenuItemResponse(
            restaurantMenuItem.getId(),
            restaurantMenuItem.getCategory().getId(),
            restaurantMenuItem.getCategory().getName(),
            restaurantMenuItem.getName(),
            restaurantMenuItem.getDescription(),
            restaurantMenuItem.getPrice(),
            restaurantMenuItem.getRestaurant().getId(),
            restaurantMenuItem.getAvailable(),
            restaurantMenuItem.getIsVegan(),
            restaurantMenuItem.getIsVegetarian(),
            restaurantMenuItem.getAllergyWarnings(),
            restaurantMenuItem.getIsTodaySpecial()
        );
    }
}
