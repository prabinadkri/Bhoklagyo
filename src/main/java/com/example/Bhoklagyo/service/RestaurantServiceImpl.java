package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.RestaurantRequest;
import com.example.Bhoklagyo.dto.RestaurantResponse;
import com.example.Bhoklagyo.entity.CuisineTag;
import com.example.Bhoklagyo.entity.DietaryTag;
import com.example.Bhoklagyo.entity.Restaurant;
import com.example.Bhoklagyo.entity.User;
import com.example.Bhoklagyo.entity.Role;
import com.example.Bhoklagyo.entity.Vendor;
import com.example.Bhoklagyo.entity.Document;
import com.example.Bhoklagyo.exception.ResourceNotFoundException;
import com.example.Bhoklagyo.mapper.RestaurantMapper;
import com.example.Bhoklagyo.repository.CuisineTagRepository;
import com.example.Bhoklagyo.repository.DietaryTagRepository;
import com.example.Bhoklagyo.repository.RestaurantRepository;
import com.example.Bhoklagyo.repository.UserRepository;
import com.example.Bhoklagyo.repository.VendorRepository;
import com.example.Bhoklagyo.repository.DocumentRepository;
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
    private final VendorRepository vendorRepository;
    private final DocumentRepository documentRepository;
    
    public RestaurantServiceImpl(RestaurantRepository restaurantRepository,
                                CuisineTagRepository cuisineTagRepository,
                                DietaryTagRepository dietaryTagRepository,
                                UserRepository userRepository,
                                RestaurantMapper restaurantMapper,
                                VendorRepository vendorRepository,
                                DocumentRepository documentRepository) {
        this.restaurantRepository = restaurantRepository;
        this.cuisineTagRepository = cuisineTagRepository;
        this.dietaryTagRepository = dietaryTagRepository;
        this.userRepository = userRepository;
        this.restaurantMapper = restaurantMapper;
        this.vendorRepository = vendorRepository;
        this.documentRepository = documentRepository;
    }
    
    @Override
    public RestaurantResponse createRestaurant(RestaurantRequest request) {
        // Restaurant creation is now handled by ADMIN only (enforced by @PreAuthorize in controller)
        // Admin creates restaurant with vendor and documents
        
        // Validate vendor exists by PAN number
        Vendor vendor = vendorRepository.findByPanNumber(request.getPanNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with PAN number: " + request.getPanNumber()));
        
        Restaurant restaurant = restaurantMapper.toEntity(request);
        restaurant.setVendor(vendor);
        restaurant.setContactNumber(request.getContactNumber());
        
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
        
        // Link documents if provided
        if (request.getDocumentIds() != null && !request.getDocumentIds().isEmpty()) {
            Set<Document> documents = new HashSet<>();
            for (Long docId : request.getDocumentIds()) {
                Document document = documentRepository.findById(docId)
                    .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + docId));
                documents.add(document);
                document.getRestaurants().add(restaurant);
            }
            restaurant.setDocuments(documents);
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
