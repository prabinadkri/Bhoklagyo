package com.example.Bhoklagyo.mapper;

import com.example.Bhoklagyo.dto.MenuItemResponse;
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
        
        List<MenuItemResponse> menuItemResponses = order.getOrderItems()
            .stream()
            .map(menuItemMapper::toResponse)
            .collect(Collectors.toList());
        
        return new OrderResponse(
            order.getId(),
            customerId,
            customerName,
            restaurantId,
            menuItemResponses,
            order.getStatus(),
            order.getTotalPrice()
        );
    }
}
