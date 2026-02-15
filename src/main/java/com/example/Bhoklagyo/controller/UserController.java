package com.example.Bhoklagyo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Bhoklagyo.dto.UserResponse;
import com.example.Bhoklagyo.entity.Restaurant;
import com.example.Bhoklagyo.entity.User;
import com.example.Bhoklagyo.mapper.UserMapper;
import com.example.Bhoklagyo.repository.UserRepository;
import com.example.Bhoklagyo.dto.RestaurantResponse;
import com.example.Bhoklagyo.mapper.RestaurantMapper;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RestaurantMapper restaurantMapper;

    public UserController(UserRepository userRepository, UserMapper userMapper, RestaurantMapper restaurantMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.restaurantMapper = restaurantMapper;
    }
    @GetMapping
    public ResponseEntity<UserResponse> getUserByEmail(@RequestParam String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return ResponseEntity.ok(userMapper.toResponse(user));
    }
    @GetMapping("/employed-restaurant/{id}")
    public ResponseEntity<RestaurantResponse> getEmployedRestaurant(@PathVariable Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        Restaurant restaurant = user.getEmployedRestaurant();
        RestaurantResponse response = restaurantMapper.toResponse(restaurant);
        return ResponseEntity.ok(response);
    }
}
