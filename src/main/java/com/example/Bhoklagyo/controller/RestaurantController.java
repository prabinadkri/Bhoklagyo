package com.example.Bhoklagyo.controller;

import com.example.Bhoklagyo.dto.InviteEmployeeRequest;
import com.example.Bhoklagyo.dto.PaginatedRestaurantResponse;
import com.example.Bhoklagyo.entity.Restaurant;
import com.example.Bhoklagyo.entity.User;
import com.example.Bhoklagyo.repository.RestaurantRepository;
import com.example.Bhoklagyo.repository.UserRepository;
import com.example.Bhoklagyo.security.InviteTokenUtil;
import com.example.Bhoklagyo.service.EmailService;
import com.example.Bhoklagyo.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.Bhoklagyo.dto.RestaurantRequest;
import com.example.Bhoklagyo.dto.RestaurantResponse;
import com.example.Bhoklagyo.dto.MenuItemRequest;
import com.example.Bhoklagyo.dto.MenuItemResponse;
import com.example.Bhoklagyo.service.RestaurantService;
import com.example.Bhoklagyo.service.MenuItemService;
import com.example.Bhoklagyo.dto.UserResponse;
import com.example.Bhoklagyo.mapper.UserMapper;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {
    
    private final RestaurantService restaurantService;
    private final MenuItemService menuItemService;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final InviteTokenUtil inviteTokenUtil;
    private final EmailService emailService;
    private final UserMapper userMapper;
    private final FileStorageService fileStorageService;

    public RestaurantController(RestaurantService restaurantService,
                                MenuItemService menuItemService,
                                UserRepository userRepository,
                                RestaurantRepository restaurantRepository,
                                InviteTokenUtil inviteTokenUtil,
                                EmailService emailService,
                                UserMapper userMapper,
                                FileStorageService fileStorageService) {
        this.restaurantService = restaurantService;
        this.menuItemService = menuItemService;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.inviteTokenUtil = inviteTokenUtil;
        this.emailService = emailService;
        this.userMapper = userMapper;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public ResponseEntity<PaginatedRestaurantResponse> getAllRestaurants(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "20") Integer limit) {
        PaginatedRestaurantResponse restaurants = restaurantService.getAllRestaurantsPaginated(cursor, limit);
        return ResponseEntity.ok(restaurants);
    }
    
    @GetMapping("/featured")
    public ResponseEntity<List<RestaurantResponse>> getFeaturedRestaurants() {
        List<RestaurantResponse> restaurants = restaurantService.getFeaturedRestaurants();
        return ResponseEntity.ok(restaurants);
    }
    
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<RestaurantResponse>> getRestaurantsByOwnerId(@PathVariable Long ownerId) {
        List<RestaurantResponse> restaurants = restaurantService.getRestaurantsByOwnerId(ownerId);
        return ResponseEntity.ok(restaurants);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestaurantResponse> createRestaurant(@RequestBody RestaurantRequest request) {
        RestaurantResponse response = restaurantService.createRestaurant(request);
        return ResponseEntity.status(201).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getRestaurantById(@PathVariable Long id) {
        RestaurantResponse response = restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}/menu")
    public ResponseEntity<List<MenuItemResponse>> getMenuItemsByRestaurantId(@PathVariable Long id) {
        List<MenuItemResponse> menuItems = menuItemService.getMenuItemsByRestaurantId(id);
        return ResponseEntity.ok(menuItems);
    }

    @PostMapping("/{id}/menu")
    public ResponseEntity<List<MenuItemResponse>> addMenuItemsToRestaurant(
            @PathVariable Long id, 
            @RequestBody List<MenuItemRequest> menuItemRequests) {
        List<MenuItemResponse> responses = menuItemService.addMenuItemsToRestaurant(id, menuItemRequests);
        return ResponseEntity.status(201).body(responses);
    }
    
    @PatchMapping("/{id}/menu/{restaurantMenuItemId}")
    public ResponseEntity<MenuItemResponse> updateRestaurantMenuItem(
            @PathVariable Long id,
            @PathVariable Long restaurantMenuItemId, 
            @RequestBody MenuItemRequest menuItemRequest) {
        MenuItemResponse response = menuItemService.updateRestaurantMenuItem(restaurantMenuItemId, menuItemRequest);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}/menu/{restaurantMenuItemId}")
    public ResponseEntity<Void> deleteMenuItemFromRestaurant(
            @PathVariable Long id, 
            @PathVariable Long restaurantMenuItemId) {
        menuItemService.deleteRestaurantMenuItem(restaurantMenuItemId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}/employees")
    public ResponseEntity<List<UserResponse>> getEmployeesByRestaurantId(@PathVariable Long id) {
        List<UserResponse> employees = userMapper.toResponseList(
            userRepository.findEmployeesByRestaurantId(id));
        return ResponseEntity.ok(employees);
    }

        @PostMapping("/{id}/invite-employee")
        @PreAuthorize("hasRole('OWNER')")
        public ResponseEntity<?> inviteEmployee(@PathVariable Long id,
                            @RequestBody InviteEmployeeRequest request,
                            Authentication authentication) {
        String email = authentication.getName();
        User requester = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Requester not found"));

        Restaurant restaurant = restaurantRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        boolean isOwnerOfRestaurant = restaurant.getOwner() != null &&
            restaurant.getOwner().getId().equals(requester.getId());

        if (!isOwnerOfRestaurant) {
            return ResponseEntity.status(403).body(Map.of("error", "Forbidden: not restaurant owner"));
        }

        String token = inviteTokenUtil.generateInviteToken(
            restaurant.getOwner() != null ? restaurant.getOwner().getId() : requester.getId(),
            restaurant.getId(),
            request.getEmail()
        );

        emailService.sendInviteEmail(request.getEmail(), token,restaurant.getName());
        return ResponseEntity.ok(Map.of("message", "Invitation sent"));
        }

    @PostMapping("/{id}/upload-image")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<RestaurantResponse> uploadRestaurantImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile file) {
        
        // Validate file
        if (file.isEmpty()) {
            throw new RuntimeException("Please select a file to upload");
        }
        
        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Only image files are allowed");
        }
        
        // Store file and get filename
        String fileName = fileStorageService.storeFile(file);
        
        // Update restaurant with relative image path
        RestaurantResponse response = restaurantService.updateRestaurantImage(id, "/uploads/restaurant-images/" + fileName);
        
        return ResponseEntity.ok(response);
    }

    
}