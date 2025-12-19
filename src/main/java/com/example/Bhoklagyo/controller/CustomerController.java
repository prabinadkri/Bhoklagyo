package com.example.Bhoklagyo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.Bhoklagyo.dto.FeedbackRequest;
import com.example.Bhoklagyo.dto.OrderResponse;
import com.example.Bhoklagyo.service.OrderService;
import com.example.Bhoklagyo.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class CustomerController {

    private final OrderService orderService;
    private final UserRepository userRepository;
    
    public CustomerController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomerId(Authentication authentication) {
        String email = authentication.getName();
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
        
        List<OrderResponse> orders = orderService.getOrdersByCustomerId(userId);
        return ResponseEntity.ok(orders);
    }
    @PostMapping("/{orderId}/feedback")
    public ResponseEntity<OrderResponse> submitOrderFeedback(Authentication authentication, @PathVariable Long orderId, @RequestBody FeedbackRequest request) {
        String email = authentication.getName();
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
        
        OrderResponse updatedOrder = orderService.submitOrderFeedback(userId, orderId, request.getFeedback(), request.getRating());
        return ResponseEntity.ok(updatedOrder);
    }
}