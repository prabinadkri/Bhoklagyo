package com.example.Bhoklagyo.dto;

import com.example.Bhoklagyo.entity.OrderStatus;
import java.util.List;

public class OrderRequest {
    private Long customerId;
    private Long restaurantId;
    private List<OrderItemRequest> items;
    private OrderStatus status;
    private Double deliveryLatitude;
    private Double deliveryLongitude;
    private String feedback;
    private String specialRequest;

    public OrderRequest() {}

    public OrderRequest(Long customerId, Long restaurantId, List<OrderItemRequest> items, OrderStatus status, Double deliveryLatitude, Double deliveryLongitude, String feedback, String specialRequest) {
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.items = items;
        this.status = status;
        this.deliveryLatitude = deliveryLatitude;
        this.deliveryLongitude = deliveryLongitude;
        this.feedback = feedback;
        this.specialRequest = specialRequest;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
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

    public String getSpecialRequest() {
        return specialRequest;
    }

    public void setSpecialRequest(String specialRequest) {
        this.specialRequest = specialRequest;
    }
}
