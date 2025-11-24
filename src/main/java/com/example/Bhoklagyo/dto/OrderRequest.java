package com.example.Bhoklagyo.dto;

import com.example.Bhoklagyo.entity.OrderStatus;
import java.util.List;

public class OrderRequest {
    private Long customerId;
    private Long restaurantId;
    private List<Long> menuItemIds;
    private OrderStatus status;
    private Double deliveryLatitude;
    private Double deliveryLongitude;
    private String feedback;

    public OrderRequest() {}

    public OrderRequest(Long customerId, Long restaurantId, List<Long> menuItemIds, OrderStatus status, Double deliveryLatitude, Double deliveryLongitude, String feedback) {
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.menuItemIds = menuItemIds;
        this.status = status;
        this.deliveryLatitude = deliveryLatitude;
        this.deliveryLongitude = deliveryLongitude;
        this.feedback = feedback;
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

    public List<Long> getMenuItemIds() {
        return menuItemIds;
    }

    public void setMenuItemIds(List<Long> menuItemIds) {
        this.menuItemIds = menuItemIds;
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
}
