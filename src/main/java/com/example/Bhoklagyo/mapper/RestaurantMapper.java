package com.example.Bhoklagyo.mapper;

import com.example.Bhoklagyo.dto.RestaurantRequest;
import com.example.Bhoklagyo.dto.RestaurantResponse;
import com.example.Bhoklagyo.entity.Restaurant;
import org.springframework.stereotype.Component;

@Component
public class RestaurantMapper {
    
    public Restaurant toEntity(RestaurantRequest request) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(request.getName());
        return restaurant;
    }
    
    public RestaurantResponse toResponse(Restaurant restaurant) {
        return new RestaurantResponse(
            restaurant.getId(),
            restaurant.getName()
        );
    }
}
