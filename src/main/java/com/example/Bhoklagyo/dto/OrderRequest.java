package com.example.Bhoklagyo.dto;

import java.util.List;

public class OrderRequest {
    private Long customerId;
    private Long restaurantId;
    private List<Long> menuItemIds;
    private String status;

    public OrderRequest() {}

    public OrderRequest(Long customerId, Long restaurantId, List<Long> menuItemIds, String status) {
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.menuItemIds = menuItemIds;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
