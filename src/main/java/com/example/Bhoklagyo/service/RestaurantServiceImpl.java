package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.RestaurantRequest;
import com.example.Bhoklagyo.dto.RestaurantResponse;
import com.example.Bhoklagyo.entity.Restaurant;
import com.example.Bhoklagyo.exception.ResourceNotFoundException;
import com.example.Bhoklagyo.mapper.RestaurantMapper;
import com.example.Bhoklagyo.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RestaurantServiceImpl implements RestaurantService {
    
    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;
    
    public RestaurantServiceImpl(RestaurantRepository restaurantRepository, RestaurantMapper restaurantMapper) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantMapper = restaurantMapper;
    }
    
    @Override
    public RestaurantResponse createRestaurant(RestaurantRequest request) {
        Restaurant restaurant = restaurantMapper.toEntity(request);
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        return restaurantMapper.toResponse(savedRestaurant);
    }
    
    @Override
    @Transactional(readOnly = true)
    public RestaurantResponse getRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));
        return restaurantMapper.toResponse(restaurant);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getAllRestaurants() {
        return restaurantRepository.findAll()
            .stream()
            .map(restaurantMapper::toResponse)
            .collect(Collectors.toList());
    }
}
