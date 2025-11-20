package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.RestaurantRequest;
import com.example.Bhoklagyo.dto.RestaurantResponse;

import java.util.List;

public interface RestaurantService {
    RestaurantResponse createRestaurant(RestaurantRequest request);
    RestaurantResponse getRestaurantById(Long id);
    List<RestaurantResponse> getAllRestaurants();
}
