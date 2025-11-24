package com.example.Bhoklagyo.mapper;

import com.example.Bhoklagyo.dto.RestaurantRequest;
import com.example.Bhoklagyo.dto.RestaurantResponse;
import com.example.Bhoklagyo.entity.CuisineTag;
import com.example.Bhoklagyo.entity.DietaryTag;
import com.example.Bhoklagyo.entity.Restaurant;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RestaurantMapper {
    
    public Restaurant toEntity(RestaurantRequest request) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(request.getName());
        restaurant.setLatitude(request.getLatitude());
        restaurant.setLongitude(request.getLongitude());
        return restaurant;
    }
    
    public RestaurantResponse toResponse(Restaurant restaurant) {
        List<String> cuisineTagNames = restaurant.getCuisineTags().stream()
            .map(CuisineTag::getName)
            .collect(Collectors.toList());
        
        List<String> dietaryTagNames = restaurant.getDietaryTags().stream()
            .map(DietaryTag::getName)
            .collect(Collectors.toList());
        
        return new RestaurantResponse(
            restaurant.getId(),
            restaurant.getName(),
            restaurant.getLatitude(),
            restaurant.getLongitude(),
            cuisineTagNames,
            dietaryTagNames
        );
    }
}
