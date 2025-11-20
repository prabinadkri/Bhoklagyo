package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.OrderRequest;
import com.example.Bhoklagyo.dto.OrderResponse;
import com.example.Bhoklagyo.entity.MenuItem;
import com.example.Bhoklagyo.entity.Order;
import com.example.Bhoklagyo.entity.Restaurant;
import com.example.Bhoklagyo.mapper.OrderMapper;
import com.example.Bhoklagyo.repository.MenuItemRepository;
import com.example.Bhoklagyo.repository.OrderRepository;
import com.example.Bhoklagyo.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderMapper orderMapper;
    
    public OrderServiceImpl(OrderRepository orderRepository,
                           RestaurantRepository restaurantRepository,
                           MenuItemRepository menuItemRepository,
                           OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
        this.orderMapper = orderMapper;
    }
    
    @Override
    public OrderResponse createOrder(OrderRequest request) {
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
            .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + request.getRestaurantId()));
        
        List<MenuItem> menuItems = menuItemRepository.findAllById(request.getMenuItemIds());
        
        if (menuItems.size() != request.getMenuItemIds().size()) {
            throw new RuntimeException("Some menu items not found");
        }
        
        // Calculate total price from menu items
        Double totalPrice = menuItems.stream()
            .map(MenuItem::getPrice)
            .reduce(0.0, Double::sum);
        
        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setRestaurant(restaurant);
        order.setMenuItems(menuItems);
        order.setStatus(request.getStatus());
        order.setTotalPrice(totalPrice);
        
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }
    
    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        return orderMapper.toResponse(order);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByRestaurantId(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + restaurantId));
        
        return orderRepository.findByRestaurant(restaurant)
            .stream()
            .map(orderMapper::toResponse)
            .collect(Collectors.toList());
    }
}
