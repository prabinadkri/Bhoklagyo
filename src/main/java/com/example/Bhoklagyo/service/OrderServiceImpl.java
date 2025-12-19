package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.OrderRequest;
import com.example.Bhoklagyo.dto.OrderResponse;
import com.example.Bhoklagyo.entity.User;
import com.example.Bhoklagyo.entity.RestaurantMenuItem;
import com.example.Bhoklagyo.entity.Order;
import com.example.Bhoklagyo.entity.OrderItem;
import com.example.Bhoklagyo.entity.OrderStatus;
import com.example.Bhoklagyo.entity.Restaurant;
import com.example.Bhoklagyo.entity.Role;
import com.example.Bhoklagyo.exception.ResourceNotFoundException;
import com.example.Bhoklagyo.mapper.OrderMapper;
import com.example.Bhoklagyo.repository.UserRepository;
import com.example.Bhoklagyo.repository.RestaurantMenuItemRepository;
import com.example.Bhoklagyo.repository.OrderRepository;
import com.example.Bhoklagyo.repository.RestaurantRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final com.example.Bhoklagyo.websocket.OrderEventSender orderEventSender;
    
    public OrderServiceImpl(OrderRepository orderRepository,
                           RestaurantRepository restaurantRepository,
                           RestaurantMenuItemRepository restaurantMenuItemRepository,
                           UserRepository userRepository,
                           OrderMapper orderMapper,
                           com.example.Bhoklagyo.websocket.OrderEventSender orderEventSender) {
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.restaurantMenuItemRepository = restaurantMenuItemRepository;
        this.userRepository = userRepository;
        this.orderMapper = orderMapper;
        this.orderEventSender = orderEventSender;
    }
    
    @Override
    public OrderResponse createOrder(Long restaurantId, OrderRequest request) {
        User customer = userRepository.findById(request.getCustomerId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getCustomerId()));
        
        // Authorization: Only the customer themselves can create an order
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(customer.getId())) {
            throw new AccessDeniedException("You can only create orders for yourself");
        }
        
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));
        
        // Create order first
        Order order = new Order();
        order.setCustomer(customer);
        order.setRestaurant(restaurant);
        order.setStatus(request.getStatus());
        order.setDeliveryLatitude(request.getDeliveryLatitude());
        order.setDeliveryLongitude(request.getDeliveryLongitude());
        order.setFeedback(request.getFeedback());
        order.setSpecialRequest(request.getSpecialRequest());
        order.setOrderTime(LocalDateTime.now());
        
        // Create OrderItem entities with quantities and calculate total
        double totalPrice = 0.0;
        for (var itemRequest : request.getItems()) {
            RestaurantMenuItem menuItem = restaurantMenuItemRepository.findById(itemRequest.getMenuItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + itemRequest.getMenuItemId()));
            
            // Validate that the menu item belongs to the specified restaurant
            if (!menuItem.getRestaurant().getId().equals(restaurantId)) {
                throw new ResourceNotFoundException("Menu item with id " + itemRequest.getMenuItemId() + " does not belong to restaurant with id " + restaurantId);
            }
            
            Double priceAtOrder = menuItem.getEffectivePrice();
            OrderItem orderItem = new OrderItem(order, menuItem, itemRequest.getQuantity(), priceAtOrder);
            order.addOrderItem(orderItem);
            
            totalPrice += orderItem.getSubtotal();
        }
        
        order.setTotalPrice(totalPrice);
        
        Order savedOrder = orderRepository.save(order);
        OrderResponse response = orderMapper.toResponse(savedOrder);

        // Notify the customer (user-specific queue) and the restaurant topic
        try {
            // At creation: send CREATED notification to restaurant only
            // (no customer websocket notification on create to avoid duplicate/extra notifications)
            orderEventSender.sendOrderCreatedToRestaurant(restaurant.getId(), response);
        } catch (Exception e) {
            // Do not fail the request if notification fails; just log
            System.err.println("Failed to send order websocket event: " + e.getMessage());
        }

        return response;
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
        
        // Authorization: Check if user has permission to view this order
        User currentUser = getCurrentUser();
        
        // Customers can only see their own orders
        if (currentUser.getRole() == Role.CUSTOMER) {
            if (!order.getCustomer().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("You can only view your own orders");
            }
        }
        // Owners and employees can only see orders from their restaurant
        else if (currentUser.getRole() == Role.OWNER || currentUser.getRole() == Role.EMPLOYEE) {
            verifyRestaurantAccess(order.getRestaurant());
        }
        
        return orderMapper.toResponse(order);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByRestaurantId(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + restaurantId));
        
        // Authorization: Only restaurant owner or employees can view restaurant orders
        verifyRestaurantAccess(restaurant);
        
        return orderRepository.findByRestaurant(restaurant)
            .stream()
            .map(orderMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCustomerId(Long customerId) {
        // Authorization: Users can only view their own orders
        
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(customerId)) {
            throw new AccessDeniedException("You can only view your own orders");
        }
        
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
        
        // Authorization: Only restaurant owner or employees can update order status
        verifyRestaurantAccess(order.getRestaurant());
        
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        OrderResponse response = orderMapper.toResponse(updatedOrder);

        // Notify interested parties about status update
        try {
            // On status update: notify the customer only (restaurant already knows about order)
            orderEventSender.sendOrderToCustomer(updatedOrder.getCustomer().getId(), response);
        } catch (Exception e) {
            System.err.println("Failed to send order status websocket event: " + e.getMessage());
        }

        return response;
    }

    @Override
    public OrderResponse submitOrderFeedback(Long customerId, Long orderId, String feedback, Integer rating) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        // Validate that the order belongs to the specified customer
        if (!order.getCustomer().getId().equals(customerId)) {
            throw new ResourceNotFoundException("Order with id " + orderId + " does not belong to customer with id " + customerId);
        }

        // Authorization: Users can only submit feedback for their own orders
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(customerId)) {
            throw new AccessDeniedException("You can only submit feedback for your own orders");
        }

        // Set feedback text
        order.setFeedback(feedback);

        // Handle rating if provided (1-5)
        if (rating != null) {
            if (rating < 1 || rating > 5) {
                throw new IllegalArgumentException("Rating must be between 1 and 5");
            }

            Restaurant restaurant = order.getRestaurant();
            if (restaurant == null) {
                throw new ResourceNotFoundException("Associated restaurant not found for order id: " + orderId);
            }

            Long prevTotal = restaurant.getTotalRating() != null ? restaurant.getTotalRating() : 0L;
            Long prevCount = restaurant.getTotalCount() != null ? restaurant.getTotalCount() : 0L;
            Integer prevOrderRating = order.getRating();

            if (prevOrderRating == null) {
                // New rating
                prevTotal += rating;
                prevCount += 1;
            } else {
                // Overwrite existing rating for this order
                prevTotal = prevTotal - prevOrderRating + rating;
            }

            // Update restaurant aggregates
            restaurant.setTotalRating(prevTotal);
            restaurant.setTotalCount(prevCount);
            double avg = prevCount > 0 ? ((double) prevTotal) / prevCount : 0.0;
            restaurant.setRating(avg);

            // Persist restaurant changes
            restaurantRepository.save(restaurant);

            // Persist rating on order
            order.setRating(rating);
        }

        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toResponse(updatedOrder);
    }

    @Override
    public OrderResponse submitOrderFeedback(Long customerId, Long orderId, String feedback) {
        return submitOrderFeedback(customerId, orderId, feedback, null);
    }
    
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }
    
    private void verifyRestaurantAccess(Restaurant restaurant) {
        User currentUser = getCurrentUser();
        
        // Check if user is the restaurant owner
        boolean isOwner = restaurant.getOwner() != null && 
                         restaurant.getOwner().getId().equals(currentUser.getId());
        
        // Check if user is an employee of the restaurant
        boolean isEmployee = currentUser.getRole() == Role.EMPLOYEE && 
                            currentUser.getEmployedRestaurant() != null && 
                            currentUser.getEmployedRestaurant().getId().equals(restaurant.getId());
        
        if (!isOwner && !isEmployee) {
            throw new AccessDeniedException("You are not authorized to access orders for this restaurant. Only the restaurant owner or employees can perform this action.");
        }
    }
}