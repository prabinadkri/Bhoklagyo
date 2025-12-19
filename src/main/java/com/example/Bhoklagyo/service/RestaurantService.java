package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.PaginatedRestaurantResponse;
import com.example.Bhoklagyo.dto.RestaurantRequest;
import com.example.Bhoklagyo.dto.RestaurantResponse;

import java.util.List;

public interface RestaurantService {
    RestaurantResponse createRestaurant(RestaurantRequest request);
    RestaurantResponse getRestaurantById(Long id);
    List<RestaurantResponse> getAllRestaurants();
    PaginatedRestaurantResponse getAllRestaurantsPaginated(Long cursor, Integer limit);
    List<RestaurantResponse> getFeaturedRestaurants();
    List<RestaurantResponse> getRestaurantsByOwnerId(Long ownerId);
    RestaurantResponse updateRestaurantImage(Long restaurantId, String imagePath);
    RestaurantResponse setRestaurantIsOpen(Long restaurantId, Boolean isOpen);
}
