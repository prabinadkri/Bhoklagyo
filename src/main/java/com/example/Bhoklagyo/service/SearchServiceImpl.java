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
        
        // Results are already sorted by ID from repository
        boolean hasMore = mergedRestaurants.size() > limit;
        List<Restaurant> finalRestaurants = hasMore ? mergedRestaurants.subList(0, limit) : mergedRestaurants;
        
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

    @Override
    public PaginatedSearchResultResponse searchPaginatedByLocation(String keyword, Double latitude, Double longitude, Long cursorId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 20;
        }

        // Fetch one extra to determine if there are more results
        int fetch = limit + 1;

        double lat = (latitude == null) ? 0.0 : latitude;
        double lon = (longitude == null) ? 0.0 : longitude;

        List<Restaurant> restaurants = restaurantRepository.searchByKeywordOrderByDistanceNativeWithCursor(keyword, lat, lon, cursorId, fetch);

        boolean hasMore = restaurants.size() > limit;
        List<Restaurant> finalRestaurants = hasMore ? restaurants.subList(0, limit) : restaurants;

        List<RestaurantResponse> restaurantResponses = finalRestaurants.stream()
            .map(restaurantMapper::toResponse)
            .collect(Collectors.toList());

        Long nextCursor = null;
        if (hasMore && !finalRestaurants.isEmpty()) {
            nextCursor = finalRestaurants.get(finalRestaurants.size() - 1).getId();
        }

        PaginatedRestaurantResponse paginatedRestaurants = new PaginatedRestaurantResponse(restaurantResponses, nextCursor, hasMore);

        // Menu items: return matches ordered by nearest restaurant first (not paginated)
        List<RestaurantMenuItem> menuItems = restaurantMenuItemRepository.searchByNameOrDescriptionOrderByRestaurantDistance(keyword, lat, lon, limit);
        List<MenuItemResponse> menuItemResponses = menuItems.stream()
            .map(menuItemMapper::toResponse)
            .collect(Collectors.toList());

        return new PaginatedSearchResultResponse(paginatedRestaurants, new PaginatedSearchResultResponse.MenuItemSearchResponse(menuItemResponses));
    }
    @Override
    public PaginatedSearchResultResponse searchPaginatedByRating(String keyword, Long cursor, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 20;
        }
        // Fetch one extra to determine if there are more results
        Pageable pageable = PageRequest.of(0, limit + 1);

        List<Restaurant> restaurantsByName;
        List<Restaurant> restaurantsByCuisine;

        if (cursor == null) {
            restaurantsByName = restaurantRepository.searchByNameRated(keyword, pageable);
            restaurantsByCuisine = restaurantRepository.searchByCuisineTagRated(keyword, pageable);
        } else {
            // Resolve cursor rating from restaurant id
            Double cursorRating = 0.0;
            try {
                Restaurant cursorRestaurant = restaurantRepository.findById(cursor).orElse(null);
                if (cursorRestaurant != null && cursorRestaurant.getRating() != null) {
                    cursorRating = cursorRestaurant.getRating();
                }
            } catch (Exception e) {
                cursorRating = 0.0;
            }

            restaurantsByName = restaurantRepository.searchByNameWithCursorRated(keyword, cursorRating, cursor, pageable);
            restaurantsByCuisine = restaurantRepository.searchByCuisineTagWithCursorRated(keyword, cursorRating, cursor, pageable);
        }

        // Merge and deduplicate results
        List<Restaurant> mergedRestaurants = restaurantsByName.stream()
                .collect(Collectors.toList());

        restaurantsByCuisine.stream()
                .filter(r -> mergedRestaurants.stream().noneMatch(existing -> existing.getId().equals(r.getId())))
                .forEach(mergedRestaurants::add);

        // Results are already sorted by rating DESC, id ASC from repository
        boolean hasMore = mergedRestaurants.size() > limit;
        List<Restaurant> finalRestaurants = hasMore ? mergedRestaurants.subList(0, limit) : mergedRestaurants;

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
    @Override
    public PaginatedSearchResultResponse searchPaginatedByPrice(String keyword, Long cursor, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 20;
        }
        // Fetch one extra to determine if there are more results
        Pageable pageable = PageRequest.of(0, limit + 1);

        List<Restaurant> restaurantsByName;
        List<Restaurant> restaurantsByCuisine;

        if (cursor == null) {
            restaurantsByName = restaurantRepository.searchByNameOrderedByPrice(keyword, pageable);
            restaurantsByCuisine = restaurantRepository.searchByCuisineTagOrderedByPrice(keyword, pageable);
        } else {
            restaurantsByName = restaurantRepository.searchByNameWithCursorOrderedByPrice(keyword, cursor, pageable);
            restaurantsByCuisine = restaurantRepository.searchByCuisineTagWithCursorOrderedByPrice(keyword, cursor, pageable);
        }

        // Merge and deduplicate results
        List<Restaurant> mergedRestaurants = restaurantsByName.stream()
                .collect(Collectors.toList());

        restaurantsByCuisine.stream()
                .filter(r -> mergedRestaurants.stream().noneMatch(existing -> existing.getId().equals(r.getId())))
                .forEach(mergedRestaurants::add);

        // Results are already sorted by price ASC, id ASC from repository
        boolean hasMore = mergedRestaurants.size() > limit;
        List<Restaurant> finalRestaurants = hasMore ? mergedRestaurants.subList(0, limit) : mergedRestaurants;

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