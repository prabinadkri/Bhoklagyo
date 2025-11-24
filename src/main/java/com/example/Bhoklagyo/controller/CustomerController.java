package com.example.Bhoklagyo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.Bhoklagyo.dto.FeedbackRequest;
import com.example.Bhoklagyo.dto.OrderResponse;
import com.example.Bhoklagyo.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class CustomerController {

    private final OrderService orderService;
    
    public CustomerController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{userid}/orders")
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomerId(@PathVariable Long userid) {
        List<OrderResponse> orders = orderService.getOrdersByCustomerId(userid);
        return ResponseEntity.ok(orders);
    }
    @PostMapping("/{userid}/orders/{orderId}/feedback")
    public ResponseEntity<OrderResponse> submitOrderFeedback(@PathVariable Long userid, @PathVariable Long orderId, @RequestBody FeedbackRequest request) {
        OrderResponse updatedOrder = orderService.submitOrderFeedback(userid, orderId, request.getFeedback());
        return ResponseEntity.ok(updatedOrder);
    }
}
