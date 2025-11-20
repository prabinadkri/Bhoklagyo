package com.example.Bhoklagyo.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.example.Bhoklagyo.dto.RestaurantRequest;
import com.example.Bhoklagyo.dto.RestaurantResponse;
import com.example.Bhoklagyo.dto.MenuItemRequest;
import com.example.Bhoklagyo.dto.MenuItemResponse;
import com.example.Bhoklagyo.service.RestaurantService;
import com.example.Bhoklagyo.service.MenuItemService;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {
    
    private final RestaurantService restaurantService;
    private final MenuItemService menuItemService;
    
    public RestaurantController(RestaurantService restaurantService, MenuItemService menuItemService) {
        this.restaurantService = restaurantService;
        this.menuItemService = menuItemService;
    }

    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getAllRestaurants() {
        List<RestaurantResponse> restaurants = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(restaurants);
    }
    
    @PostMapping
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
    
    @PutMapping("/{id}/menu/{restaurantMenuItemId}")
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
}