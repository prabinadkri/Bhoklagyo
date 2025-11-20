package com.example.Bhoklagyo.dto;

import java.util.List;

public class OrderResponse {
    private Long id;
    private String customerName;
    private Long restaurantId;
    private List<MenuItemResponse> menuItems;
    private String status;
    private Double totalPrice;

    public OrderResponse() {}

    public OrderResponse(Long id, String customerName, Long restaurantId, List<MenuItemResponse> menuItems, String status, Double totalPrice) {
        this.id = id;
        this.customerName = customerName;
        this.restaurantId = restaurantId;
        this.menuItems = menuItems;
        this.status = status;
        this.totalPrice = totalPrice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
