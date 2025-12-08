package com.example.Bhoklagyo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Bhoklagyo.dto.UserResponse;
import com.example.Bhoklagyo.entity.User;
import com.example.Bhoklagyo.mapper.UserMapper;
import com.example.Bhoklagyo.repository.UserRepository;
@RestController
@RequestMapping("/users")
public class UserController {
    public final UserRepository userRepository;
    public final UserMapper userMapper;
    public UserController(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }
    @GetMapping
    public ResponseEntity<UserResponse> getUserByEmail(@RequestParam String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return ResponseEntity.ok(userMapper.toResponse(user));
    }
}
