package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.MenuItemRequest;
import com.example.Bhoklagyo.dto.MenuItemResponse;
import com.example.Bhoklagyo.entity.MenuItem;
import com.example.Bhoklagyo.entity.Restaurant;
import com.example.Bhoklagyo.mapper.MenuItemMapper;
import com.example.Bhoklagyo.repository.MenuItemRepository;
import com.example.Bhoklagyo.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MenuItemServiceImpl implements MenuItemService {
    
    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemMapper menuItemMapper;
    
    public MenuItemServiceImpl(MenuItemRepository menuItemRepository, 
                              RestaurantRepository restaurantRepository,
                              MenuItemMapper menuItemMapper) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
        this.menuItemMapper = menuItemMapper;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getMenuItemsByRestaurantId(Long restaurantId) {
        return menuItemRepository.findByRestaurantId(restaurantId)
            .stream()
            .map(menuItemMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<MenuItemResponse> updateMenuItemsForRestaurant(Long restaurantId, List<MenuItemRequest> menuItemRequests) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + restaurantId));
        
        List<MenuItem> menuItems = menuItemRequests.stream()
            .map(request -> menuItemMapper.toEntity(request, restaurant))
            .collect(Collectors.toList());
        
        List<MenuItem> savedItems = menuItemRepository.saveAll(menuItems);
        
        return savedItems.stream()
            .map(menuItemMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public void deleteMenuItem(Long restaurantId, Long menuItemId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + restaurantId));
        
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
            .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + menuItemId));
        
        if (!menuItem.getRestaurant().getId().equals(restaurantId)) {
            throw new RuntimeException("Menu item does not belong to this restaurant");
        }
        
        menuItemRepository.delete(menuItem);
    }
}
