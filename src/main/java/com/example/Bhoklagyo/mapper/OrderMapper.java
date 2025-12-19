package com.example.Bhoklagyo.mapper;

import com.example.Bhoklagyo.dto.OrderItemResponse;
import com.example.Bhoklagyo.dto.OrderResponse;

import com.example.Bhoklagyo.entity.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {
    
    private final MenuItemMapper menuItemMapper;
    
    public OrderMapper(MenuItemMapper menuItemMapper) {
        this.menuItemMapper = menuItemMapper;
    }
    
    public OrderResponse toResponse(Order order) {
        Long restaurantId = order.getRestaurant() != null ? order.getRestaurant().getId() : null;
        Long customerId = order.getCustomer() != null ? order.getCustomer().getId() : null;
        String customerName = order.getCustomer() != null ? order.getCustomer().getName() : null;
        
        List<OrderItemResponse> orderItemResponses = order.getOrderItems()
            .stream()
            .map(orderItem -> new OrderItemResponse(
                orderItem.getId(),
                menuItemMapper.toResponse(orderItem.getMenuItem()),
                orderItem.getQuantity(),
                orderItem.getPriceAtOrder(),
                orderItem.getSubtotal()
            ))
            .collect(Collectors.toList());
        
        return new OrderResponse(
            order.getId(),
            customerId,
            customerName,
            restaurantId,
            orderItemResponses,
            order.getStatus(),
            order.getTotalPrice(),
            order.getDeliveryLatitude(),
            order.getDeliveryLongitude(),
            order.getFeedback(),
            order.getRating(),
            order.getSpecialRequest(),
            order.getOrderTime()
        );
    }
}
