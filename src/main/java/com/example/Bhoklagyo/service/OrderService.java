package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.OrderRequest;
import com.example.Bhoklagyo.dto.OrderResponse;
import com.example.Bhoklagyo.entity.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(Long restaurantId, OrderRequest request);
    OrderResponse getOrderById(Long restaurantId, Long orderId);
    List<OrderResponse> getOrdersByRestaurantId(Long restaurantId);
    List<OrderResponse> getOrdersByCustomerId(Long customerId);
    OrderResponse updateOrderStatus(Long restaurantId, Long orderId, OrderStatus status);
    OrderResponse submitOrderFeedback(Long customerId, Long orderId, String feedback);
}
