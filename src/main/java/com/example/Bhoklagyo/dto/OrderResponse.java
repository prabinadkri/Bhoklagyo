package com.example.Bhoklagyo.dto;

import com.example.Bhoklagyo.entity.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {
    private Long id;
    private Long customerId;
    private String customerName;
    private Long restaurantId;
    private List<MenuItemResponse> menuItems;
    private OrderStatus status;
    private Double totalPrice;
    private Double deliveryLatitude;
    private Double deliveryLongitude;
    private String feedback;
    private LocalDateTime orderTime;

    public OrderResponse() {}

    public OrderResponse(Long id, Long customerId, String customerName, Long restaurantId, List<MenuItemResponse> menuItems, OrderStatus status, Double totalPrice, Double deliveryLatitude, Double deliveryLongitude, String feedback, LocalDateTime orderTime) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.restaurantId = restaurantId;
        this.menuItems = menuItems;
        this.status = status;
        this.totalPrice = totalPrice;
        this.deliveryLatitude = deliveryLatitude;
        this.deliveryLongitude = deliveryLongitude;
        this.feedback = feedback;
        this.orderTime = orderTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public List<MenuItemResponse> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItemResponse> menuItems) {
        this.menuItems = menuItems;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double getDeliveryLatitude() {
        return deliveryLatitude;
    }

    public void setDeliveryLatitude(Double deliveryLatitude) {
        this.deliveryLatitude = deliveryLatitude;
    }

    public Double getDeliveryLongitude() {
        return deliveryLongitude;
    }

    public void setDeliveryLongitude(Double deliveryLongitude) {
        this.deliveryLongitude = deliveryLongitude;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }
}
