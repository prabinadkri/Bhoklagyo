package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.OrderRequest;
import com.example.Bhoklagyo.dto.OrderResponse;
import com.example.Bhoklagyo.entity.Customer;
import com.example.Bhoklagyo.entity.RestaurantMenuItem;
import com.example.Bhoklagyo.entity.Order;
import com.example.Bhoklagyo.entity.OrderStatus;
import com.example.Bhoklagyo.entity.Restaurant;
import com.example.Bhoklagyo.exception.ResourceNotFoundException;
import com.example.Bhoklagyo.mapper.OrderMapper;
import com.example.Bhoklagyo.repository.CustomerRepository;
import com.example.Bhoklagyo.repository.RestaurantMenuItemRepository;
import com.example.Bhoklagyo.repository.OrderRepository;
import com.example.Bhoklagyo.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public OrderResponse createOrder(Long restaurantId, OrderRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));
        
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));
        
        // Find RestaurantMenuItem for each menuItemId and validate they belong to this restaurant
        List<RestaurantMenuItem> restaurantMenuItems = request.getMenuItemIds().stream()
            .map(menuItemId -> {
                RestaurantMenuItem menuItem = restaurantMenuItemRepository.findById(menuItemId)
                    .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + menuItemId));
                
                // Validate that the menu item belongs to the specified restaurant
                if (!menuItem.getRestaurant().getId().equals(restaurantId)) {
                    throw new ResourceNotFoundException("Menu item with id " + menuItemId + " does not belong to restaurant with id " + restaurantId);
                }
                
                return menuItem;
            })
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
        order.setDeliveryLatitude(request.getDeliveryLatitude());
        order.setDeliveryLongitude(request.getDeliveryLongitude());
        order.setFeedback(request.getFeedback());
        order.setOrderTime(LocalDateTime.now());
        
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }
    
    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long restaurantId, Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        
        // Validate that the order belongs to the specified restaurant
        if (!order.getRestaurant().getId().equals(restaurantId)) {
            throw new ResourceNotFoundException("Order with id " + orderId + " does not belong to restaurant with id " + restaurantId);
        }
        
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
    
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId)
            .stream()
            .map(orderMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public OrderResponse updateOrderStatus(Long restaurantId, Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        
        // Validate that the order belongs to the specified restaurant
        if (!order.getRestaurant().getId().equals(restaurantId)) {
            throw new ResourceNotFoundException("Order with id " + orderId + " does not belong to restaurant with id " + restaurantId);
        }
        
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toResponse(updatedOrder);
    }

    @Override
    public OrderResponse submitOrderFeedback(Long customerId, Long orderId, String feedback) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        
        // Validate that the order belongs to the specified customer
        if (!order.getCustomer().getId().equals(customerId)) {
            throw new ResourceNotFoundException("Order with id " + orderId + " does not belong to customer with id " + customerId);
        }
        
        order.setFeedback(feedback);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toResponse(updatedOrder);
    }
}