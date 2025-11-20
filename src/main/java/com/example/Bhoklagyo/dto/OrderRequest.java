package com.example.Bhoklagyo.dto;

import java.util.List;

public class OrderRequest {
    private String customerName;
    private Long restaurantId;
    private List<Long> menuItemIds;
    private String status;

    public OrderRequest() {}

    public OrderRequest(String customerName, Long restaurantId, List<Long> menuItemIds, String status) {
        this.customerName = customerName;
        this.restaurantId = restaurantId;
        this.menuItemIds = menuItemIds;
        this.status = status;
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
