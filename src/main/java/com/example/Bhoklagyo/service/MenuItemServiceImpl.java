package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.MenuItemRequest;
import com.example.Bhoklagyo.dto.MenuItemResponse;
import com.example.Bhoklagyo.entity.MenuItem;
import com.example.Bhoklagyo.entity.Restaurant;
import com.example.Bhoklagyo.entity.RestaurantMenuItem;
import com.example.Bhoklagyo.exception.DuplicateResourceException;
import com.example.Bhoklagyo.exception.ResourceNotFoundException;
import com.example.Bhoklagyo.mapper.MenuItemMapper;
import com.example.Bhoklagyo.repository.MenuItemRepository;
import com.example.Bhoklagyo.repository.RestaurantMenuItemRepository;
import com.example.Bhoklagyo.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MenuItemServiceImpl implements MenuItemService {
    
    private final MenuItemRepository menuItemRepository;
    private final RestaurantMenuItemRepository restaurantMenuItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemMapper menuItemMapper;
    
    public MenuItemServiceImpl(MenuItemRepository menuItemRepository,
                              RestaurantMenuItemRepository restaurantMenuItemRepository,
                              RestaurantRepository restaurantRepository,
                              MenuItemMapper menuItemMapper) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantMenuItemRepository = restaurantMenuItemRepository;
        this.restaurantRepository = restaurantRepository;
        this.menuItemMapper = menuItemMapper;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getMenuItemsByRestaurantId(Long restaurantId) {
        return restaurantMenuItemRepository.findByRestaurantId(restaurantId)
            .stream()
            .map(menuItemMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<MenuItemResponse> addMenuItemsToRestaurant(Long restaurantId, List<MenuItemRequest> menuItemRequests) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));
        
        return menuItemRequests.stream()
            .map(request -> {
                MenuItem menuItem;
                
                // If menuItemId is provided, use existing MenuItem by ID
                // Otherwise, check if item with same name exists, or create new one
                if (request.getMenuItemId() != null) {
                    menuItem = menuItemRepository.findById(request.getMenuItemId())
                        .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + request.getMenuItemId()));
                } else {
                    // Check if menu item with this name already exists
                    menuItem = menuItemRepository.findByName(request.getName())
                        .orElseGet(() -> {
                            // Create new base MenuItem if name doesn't exist
                            MenuItem newItem = menuItemMapper.toEntity(request);
                            return menuItemRepository.save(newItem);
                        });
                }
                
                // Check if this menu item is already added to this restaurant
                if (restaurantMenuItemRepository.existsByRestaurantIdAndMenuItemId(restaurantId, menuItem.getId())) {
                    throw new DuplicateResourceException("Menu item '" + menuItem.getName() + "' is already added to this restaurant");
                }
                
                // Create RestaurantMenuItem with restaurant-specific attributes
                RestaurantMenuItem restaurantMenuItem = new RestaurantMenuItem();
                restaurantMenuItem.setRestaurant(restaurant);
                restaurantMenuItem.setMenuItem(menuItem);
                restaurantMenuItem.setDescription(request.getDescription());
                restaurantMenuItem.setPrice(request.getPrice());
                restaurantMenuItem.setAvailable(true); // Default to available
                
                RestaurantMenuItem saved = restaurantMenuItemRepository.save(restaurantMenuItem);
                return menuItemMapper.toResponse(saved);
            })
            .collect(Collectors.toList());
    }
    
    @Override
    public MenuItemResponse updateRestaurantMenuItem(Long restaurantMenuItemId, MenuItemRequest menuItemRequest) {
        RestaurantMenuItem restaurantMenuItem = restaurantMenuItemRepository.findById(restaurantMenuItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Restaurant menu item not found with id: " + restaurantMenuItemId));
        
        // Update restaurant-specific attributes
        restaurantMenuItem.setDescription(menuItemRequest.getDescription());
        restaurantMenuItem.setPrice(menuItemRequest.getPrice());
        
        RestaurantMenuItem updated = restaurantMenuItemRepository.save(restaurantMenuItem);
        
        return menuItemMapper.toResponse(updated);
    }
    
    @Override
    public void deleteRestaurantMenuItem(Long restaurantMenuItemId) {
        RestaurantMenuItem restaurantMenuItem = restaurantMenuItemRepository.findById(restaurantMenuItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Restaurant menu item not found with id: " + restaurantMenuItemId));
        
        restaurantMenuItemRepository.delete(restaurantMenuItem);
    }
}
