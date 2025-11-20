package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.OrderRequest;
import com.example.Bhoklagyo.dto.OrderResponse;
import com.example.Bhoklagyo.entity.Customer;
import com.example.Bhoklagyo.entity.RestaurantMenuItem;
import com.example.Bhoklagyo.entity.Order;
import com.example.Bhoklagyo.entity.Restaurant;
import com.example.Bhoklagyo.exception.ResourceNotFoundException;
import com.example.Bhoklagyo.mapper.OrderMapper;
import com.example.Bhoklagyo.repository.CustomerRepository;
import com.example.Bhoklagyo.repository.RestaurantMenuItemRepository;
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
    private final RestaurantMenuItemRepository restaurantMenuItemRepository;
    private final CustomerRepository customerRepository;
    private final OrderMapper orderMapper;
    
    public OrderServiceImpl(OrderRepository orderRepository,
                           RestaurantRepository restaurantRepository,
                           RestaurantMenuItemRepository restaurantMenuItemRepository,
                           CustomerRepository customerRepository,
                           OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.restaurantMenuItemRepository = restaurantMenuItemRepository;
        this.customerRepository = customerRepository;
        this.orderMapper = orderMapper;
    }
    
    @Override
    public OrderResponse createOrder(OrderRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));
        
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
            .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + request.getRestaurantId()));
        
        // Find RestaurantMenuItem for each menuItemId in this restaurant
        List<RestaurantMenuItem> restaurantMenuItems = request.getMenuItemIds().stream()
            .map(menuItemId -> restaurantMenuItemRepository.findByRestaurantIdAndMenuItemId(request.getRestaurantId(), menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item with id " + menuItemId + " not found in restaurant with id " + request.getRestaurantId())))
            .collect(Collectors.toList());
        
        // Calculate total price from restaurant menu items
        Double totalPrice = restaurantMenuItems.stream()
            .map(RestaurantMenuItem::getPrice)
            .reduce(0.0, Double::sum);
        
        Order order = new Order();
        order.setCustomer(customer);
        order.setRestaurant(restaurant);
        order.setOrderItems(restaurantMenuItems);
        order.setStatus(request.getStatus());
        order.setTotalPrice(totalPrice);
        
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }
    
    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
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
