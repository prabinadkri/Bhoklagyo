package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.MenuItemRequest;
import com.example.Bhoklagyo.dto.MenuItemResponse;
import com.example.Bhoklagyo.entity.Category;
import com.example.Bhoklagyo.entity.Restaurant;
import com.example.Bhoklagyo.entity.RestaurantMenuItem;
import com.example.Bhoklagyo.entity.User;
import com.example.Bhoklagyo.exception.ResourceNotFoundException;
import com.example.Bhoklagyo.mapper.MenuItemMapper;
import com.example.Bhoklagyo.repository.CategoryRepository;
import com.example.Bhoklagyo.repository.RestaurantMenuItemRepository;
import com.example.Bhoklagyo.repository.RestaurantRepository;
import com.example.Bhoklagyo.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UserRepository userRepository;
    private final MenuItemMapper menuItemMapper;
    
    public MenuItemServiceImpl(CategoryRepository categoryRepository,
                              RestaurantMenuItemRepository restaurantMenuItemRepository,
                              RestaurantRepository restaurantRepository,
                              UserRepository userRepository,
                              MenuItemMapper menuItemMapper) {
        this.categoryRepository = categoryRepository;
        this.restaurantMenuItemRepository = restaurantMenuItemRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
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
        
        // Authorization check: Only the restaurant owner can add menu items
        verifyRestaurantOwner(restaurant);
        
        return menuItemRequests.stream()
            .map(request -> {
                Category category;
                
                // If categoryId is provided, use existing Category by ID
                // Otherwise, check if category with same name exists, or create new one
                if (request.getCategoryId() != null) {
                    category = categoryRepository.findById(request.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
                } else {
                    // Normalize category name: capitalize first letter of each word
                    String normalizedCategoryName = capitalizeCategoryName(request.getCategoryName());
                    
                    // Check if category with this name already exists (case-insensitive)
                    category = categoryRepository.findByNameIgnoreCase(normalizedCategoryName)
                        .orElseGet(() -> {
                            // Create new base Category if name doesn't exist
                            Category newCategory = new Category();
                            newCategory.setName(normalizedCategoryName);
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
        
        // Authorization check: Only the restaurant owner can update menu items
        verifyRestaurantOwner(restaurantMenuItem.getRestaurant());
        
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
        if (menuItemRequest.getAvailable() != null) {
            restaurantMenuItem.setAvailable(menuItemRequest.getAvailable());
        }
        
        RestaurantMenuItem updated = restaurantMenuItemRepository.save(restaurantMenuItem);
        
        return menuItemMapper.toResponse(updated);
    }
    
    @Override
    public void deleteRestaurantMenuItem(Long restaurantMenuItemId) {
        RestaurantMenuItem restaurantMenuItem = restaurantMenuItemRepository.findById(restaurantMenuItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Restaurant menu item not found with id: " + restaurantMenuItemId));
        
        // Authorization check: Only the restaurant owner can delete menu items
        verifyRestaurantOwner(restaurantMenuItem.getRestaurant());
        
        restaurantMenuItemRepository.delete(restaurantMenuItem);
    }
    
    private void verifyRestaurantOwner(Restaurant restaurant) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        
        if (restaurant.getOwner() == null || !restaurant.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not authorized to modify menu items for this restaurant. Only the restaurant owner can perform this action.");
        }
    }
    
    private String capitalizeCategoryName(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return categoryName;
        }
        
        String[] words = categoryName.trim().split("\\s+");
        StringBuilder result = new StringBuilder();
        
        for (String word : words) {
            if (word.length() > 0) {
                result.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    result.append(word.substring(1).toLowerCase());
                }
                result.append(" ");
            }
        }
        
        return result.toString().trim();
    }
}
