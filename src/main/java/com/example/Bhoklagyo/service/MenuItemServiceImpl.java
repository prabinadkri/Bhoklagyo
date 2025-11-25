package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.MenuItemRequest;
import com.example.Bhoklagyo.dto.MenuItemResponse;
import com.example.Bhoklagyo.entity.Category;
import com.example.Bhoklagyo.entity.Restaurant;
import com.example.Bhoklagyo.entity.RestaurantMenuItem;
import com.example.Bhoklagyo.exception.ResourceNotFoundException;
import com.example.Bhoklagyo.mapper.MenuItemMapper;
import com.example.Bhoklagyo.repository.CategoryRepository;
import com.example.Bhoklagyo.repository.RestaurantMenuItemRepository;
import com.example.Bhoklagyo.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MenuItemServiceImpl implements MenuItemService {
    
    private final CategoryRepository categoryRepository;
    private final RestaurantMenuItemRepository restaurantMenuItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemMapper menuItemMapper;
    
    public MenuItemServiceImpl(CategoryRepository categoryRepository,
                              RestaurantMenuItemRepository restaurantMenuItemRepository,
                              RestaurantRepository restaurantRepository,
                              MenuItemMapper menuItemMapper) {
        this.categoryRepository = categoryRepository;
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
                Category category;
                
                // If categoryId is provided, use existing Category by ID
                // Otherwise, check if category with same name exists, or create new one
                if (request.getCategoryId() != null) {
                    category = categoryRepository.findById(request.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
                } else {
                    // Check if category with this name already exists
                    category = categoryRepository.findByName(request.getCategoryName())
                        .orElseGet(() -> {
                            // Create new base Category if name doesn't exist
                            Category newCategory = menuItemMapper.toEntity(request);
                            return categoryRepository.save(newCategory);
                        });
                }
                
                // Create RestaurantMenuItem with restaurant-specific attributes
                RestaurantMenuItem restaurantMenuItem = new RestaurantMenuItem();
                restaurantMenuItem.setRestaurant(restaurant);
                restaurantMenuItem.setCategory(category);
                restaurantMenuItem.setName(request.getName());
                restaurantMenuItem.setDescription(request.getDescription());
                restaurantMenuItem.setPrice(request.getPrice());
                restaurantMenuItem.setDiscountedPrice(request.getDiscountedPrice());
                restaurantMenuItem.setAvailable(true); // Default to available
                restaurantMenuItem.setIsVegan(request.getIsVegan() != null ? request.getIsVegan() : false);
                restaurantMenuItem.setIsVegetarian(request.getIsVegetarian() != null ? request.getIsVegetarian() : false);
                restaurantMenuItem.setAllergyWarnings(request.getAllergyWarnings());
                restaurantMenuItem.setIsTodaySpecial(request.getIsTodaySpecial() != null ? request.getIsTodaySpecial() : false);
                
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
        restaurantMenuItem.setName(menuItemRequest.getName());
        restaurantMenuItem.setDescription(menuItemRequest.getDescription());
        restaurantMenuItem.setPrice(menuItemRequest.getPrice());
        restaurantMenuItem.setDiscountedPrice(menuItemRequest.getDiscountedPrice());
        if (menuItemRequest.getIsVegan() != null) {
            restaurantMenuItem.setIsVegan(menuItemRequest.getIsVegan());
        }
        if (menuItemRequest.getIsVegetarian() != null) {
            restaurantMenuItem.setIsVegetarian(menuItemRequest.getIsVegetarian());
        }
        restaurantMenuItem.setAllergyWarnings(menuItemRequest.getAllergyWarnings());
        if (menuItemRequest.getIsTodaySpecial() != null) {
            restaurantMenuItem.setIsTodaySpecial(menuItemRequest.getIsTodaySpecial());
        }
        
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
