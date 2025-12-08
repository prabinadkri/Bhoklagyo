package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.MenuItemResponse;
import com.example.Bhoklagyo.dto.PaginatedRestaurantResponse;
import com.example.Bhoklagyo.dto.PaginatedSearchResultResponse;
import com.example.Bhoklagyo.dto.RestaurantResponse;
import com.example.Bhoklagyo.dto.SearchResultResponse;
import com.example.Bhoklagyo.entity.Restaurant;
import com.example.Bhoklagyo.entity.RestaurantMenuItem;
import com.example.Bhoklagyo.mapper.MenuItemMapper;
import com.example.Bhoklagyo.mapper.RestaurantMapper;
import com.example.Bhoklagyo.repository.RestaurantMenuItemRepository;
import com.example.Bhoklagyo.repository.RestaurantRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        List<Restaurant> restaurantsByName = restaurantRepository.searchByName(keyword);
        
        // Search restaurants by cuisine tag
        List<Restaurant> restaurantsByCuisine = restaurantRepository.searchByCuisineTag(keyword);
        
        // Merge and deduplicate results
        List<Restaurant> allRestaurants = restaurantsByName.stream()
            .collect(Collectors.toList());
        
        restaurantsByCuisine.stream()
            .filter(r -> allRestaurants.stream().noneMatch(existing -> existing.getId().equals(r.getId())))
            .forEach(allRestaurants::add);
        
        List<RestaurantResponse> restaurantResponses = allRestaurants.stream()
            .map(restaurantMapper::toResponse)
            .collect(Collectors.toList());
        
        // Search menu items by name or description
        List<RestaurantMenuItem> menuItems = restaurantMenuItemRepository.searchByNameOrDescription(keyword);
        List<MenuItemResponse> menuItemResponses = menuItems.stream()
            .map(menuItemMapper::toResponse)
            .collect(Collectors.toList());
        
        return new SearchResultResponse(restaurantResponses, menuItemResponses);
    }
    
    @Override
    public PaginatedSearchResultResponse searchPaginated(String keyword, Long cursor, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 20;
        }
        // Fetch one extra to determine if there are more results
        Pageable pageable = PageRequest.of(0, limit + 1);
        
        List<Restaurant> restaurantsByName;
        List<Restaurant> restaurantsByCuisine;
        
        if (cursor == null) {
            restaurantsByName = restaurantRepository.searchByNameOrdered(keyword, pageable);
            restaurantsByCuisine = restaurantRepository.searchByCuisineTagOrdered(keyword, pageable);
        } else {
            restaurantsByName = restaurantRepository.searchByNameWithCursor(keyword, cursor, pageable);
            restaurantsByCuisine = restaurantRepository.searchByCuisineTagWithCursor(keyword, cursor, pageable);
        }
        
        // Merge and deduplicate results
        List<Restaurant> mergedRestaurants = restaurantsByName.stream()
            .collect(Collectors.toList());
        
        restaurantsByCuisine.stream()
            .filter(r -> mergedRestaurants.stream().noneMatch(existing -> existing.getId().equals(r.getId())))
            .forEach(mergedRestaurants::add);
        
        // Sort by ID and limit
        List<Restaurant> sortedRestaurants = mergedRestaurants.stream()
            .sorted((r1, r2) -> r1.getId().compareTo(r2.getId()))
            .collect(Collectors.toList());
        
        boolean hasMore = sortedRestaurants.size() > limit;
        List<Restaurant> finalRestaurants = hasMore ? sortedRestaurants.subList(0, limit) : sortedRestaurants;
        
        List<RestaurantResponse> restaurantResponses = finalRestaurants.stream()
            .map(restaurantMapper::toResponse)
            .collect(Collectors.toList());
        
        Long nextCursor = null;
        if (hasMore && !finalRestaurants.isEmpty()) {
            nextCursor = finalRestaurants.get(finalRestaurants.size() - 1).getId();
        }
        
        PaginatedRestaurantResponse paginatedRestaurants = new PaginatedRestaurantResponse(restaurantResponses, nextCursor, hasMore);
        
        // Search menu items (not paginated for now)
        List<RestaurantMenuItem> menuItems = restaurantMenuItemRepository.searchByNameOrDescription(keyword);
        List<MenuItemResponse> menuItemResponses = menuItems.stream()
            .map(menuItemMapper::toResponse)
            .collect(Collectors.toList());
        
        return new PaginatedSearchResultResponse(paginatedRestaurants, new PaginatedSearchResultResponse.MenuItemSearchResponse(menuItemResponses));
    }
}
