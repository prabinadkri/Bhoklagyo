package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.MenuItemResponse;
import com.example.Bhoklagyo.dto.RestaurantResponse;
import com.example.Bhoklagyo.dto.SearchResultResponse;
import com.example.Bhoklagyo.entity.Restaurant;
import com.example.Bhoklagyo.entity.RestaurantMenuItem;
import com.example.Bhoklagyo.mapper.MenuItemMapper;
import com.example.Bhoklagyo.mapper.RestaurantMapper;
import com.example.Bhoklagyo.repository.RestaurantMenuItemRepository;
import com.example.Bhoklagyo.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SearchServiceImpl implements SearchService {
    
    private final RestaurantRepository restaurantRepository;
    private final RestaurantMenuItemRepository restaurantMenuItemRepository;
    private final RestaurantMapper restaurantMapper;
    private final MenuItemMapper menuItemMapper;
    
    public SearchServiceImpl(RestaurantRepository restaurantRepository,
                            RestaurantMenuItemRepository restaurantMenuItemRepository,
                            RestaurantMapper restaurantMapper,
                            MenuItemMapper menuItemMapper) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantMenuItemRepository = restaurantMenuItemRepository;
        this.restaurantMapper = restaurantMapper;
        this.menuItemMapper = menuItemMapper;
    }
    
    @Override
    public SearchResultResponse search(String keyword) {
        // Search restaurants by name
        List<Restaurant> restaurants = restaurantRepository.searchByName(keyword);
        List<RestaurantResponse> restaurantResponses = restaurants.stream()
            .map(restaurantMapper::toResponse)
            .collect(Collectors.toList());
        
        // Search menu items by name or description
        List<RestaurantMenuItem> menuItems = restaurantMenuItemRepository.searchByNameOrDescription(keyword);
        List<MenuItemResponse> menuItemResponses = menuItems.stream()
            .map(menuItemMapper::toResponse)
            .collect(Collectors.toList());
        
        return new SearchResultResponse(restaurantResponses, menuItemResponses);
    }
}
