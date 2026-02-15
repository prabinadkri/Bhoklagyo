package com.example.Bhoklagyo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.Bhoklagyo.dto.OrderRequest;
import com.example.Bhoklagyo.dto.OrderResponse;
import com.example.Bhoklagyo.dto.OrderStatusRequest;
import com.example.Bhoklagyo.service.OrderService;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/restaurants/{restaurantId}/orders")
public class OrderController {

    private final OrderService orderService;
    
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrdersByRestaurantId(@PathVariable Long restaurantId) {
        List<OrderResponse> orders = orderService.getOrdersByRestaurantId(restaurantId);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long restaurantId, @PathVariable Long orderId) {
        OrderResponse order = orderService.getOrderById(restaurantId, orderId);
        return ResponseEntity.ok(order);
    }
    @PatchMapping("/{orderId}")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Long restaurantId, @PathVariable Long orderId, @Valid @RequestBody OrderStatusRequest request) {
        OrderResponse updatedOrder = orderService.updateOrderStatus(restaurantId, orderId, request.getStatus());
        return ResponseEntity.ok(updatedOrder);
    }
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@PathVariable Long restaurantId, @Valid @RequestBody OrderRequest request) {
        OrderResponse order = orderService.createOrder(restaurantId, request);
        return ResponseEntity.status(201).body(order);
    }
}
