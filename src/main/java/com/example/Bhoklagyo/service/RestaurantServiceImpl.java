package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.RestaurantRequest;
import com.example.Bhoklagyo.dto.RestaurantResponse;
import com.example.Bhoklagyo.entity.CuisineTag;
import com.example.Bhoklagyo.entity.DietaryTag;
import com.example.Bhoklagyo.entity.Restaurant;
import com.example.Bhoklagyo.entity.User;
import com.example.Bhoklagyo.entity.Role;
import com.example.Bhoklagyo.exception.ResourceNotFoundException;
import com.example.Bhoklagyo.mapper.RestaurantMapper;
import com.example.Bhoklagyo.repository.CuisineTagRepository;
import com.example.Bhoklagyo.repository.DietaryTagRepository;
import com.example.Bhoklagyo.repository.RestaurantRepository;
import com.example.Bhoklagyo.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class RestaurantServiceImpl implements RestaurantService {
    
    private final RestaurantRepository restaurantRepository;
    private final CuisineTagRepository cuisineTagRepository;
    private final DietaryTagRepository dietaryTagRepository;
    private final UserRepository userRepository;
    private final RestaurantMapper restaurantMapper;
    
    public RestaurantServiceImpl(RestaurantRepository restaurantRepository,
                                CuisineTagRepository cuisineTagRepository,
                                DietaryTagRepository dietaryTagRepository,
                                UserRepository userRepository,
                                RestaurantMapper restaurantMapper) {
        this.restaurantRepository = restaurantRepository;
        this.cuisineTagRepository = cuisineTagRepository;
        this.dietaryTagRepository = dietaryTagRepository;
        this.userRepository = userRepository;
        this.restaurantMapper = restaurantMapper;
    }
    
    @Override
    public RestaurantResponse createRestaurant(RestaurantRequest request) {
        // Authorization: Only users with OWNER role can create restaurants
        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.OWNER) {
            throw new AccessDeniedException("Only users with OWNER role can create restaurants");
        }
        
        Restaurant restaurant = restaurantMapper.toEntity(request);
        
        // Set the current user as the owner
        restaurant.setOwner(currentUser);
        
        // Process cuisine tags - find existing or create new
        if (request.getCuisineTags() != null && !request.getCuisineTags().isEmpty()) {
            Set<CuisineTag> cuisineTags = new HashSet<>();
            for (String tagName : request.getCuisineTags()) {
                CuisineTag tag = cuisineTagRepository.findByName(tagName)
                    .orElseGet(() -> cuisineTagRepository.save(new CuisineTag(tagName)));
                cuisineTags.add(tag);
            }
            restaurant.setCuisineTags(cuisineTags);
        }
        
        // Process dietary tags - find existing or create new
        if (request.getDietaryTags() != null && !request.getDietaryTags().isEmpty()) {
            Set<DietaryTag> dietaryTags = new HashSet<>();
            for (String tagName : request.getDietaryTags()) {
                DietaryTag tag = dietaryTagRepository.findByName(tagName)
                    .orElseGet(() -> dietaryTagRepository.save(new DietaryTag(tagName)));
                dietaryTags.add(tag);
            }
            restaurant.setDietaryTags(dietaryTags);
        }
        
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
    
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }
}
