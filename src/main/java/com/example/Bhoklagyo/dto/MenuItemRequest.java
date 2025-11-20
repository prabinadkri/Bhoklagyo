package com.example.Bhoklagyo.dto;

public class MenuItemRequest {
    private Long menuItemId;  // Reference to base MenuItem, null if creating new
    private String name;      // For creating new base MenuItem
    private String description;
    private Double price;

    public MenuItemRequest() {}

    public MenuItemRequest(Long menuItemId, String name, String description, Double price) {
        this.menuItemId = menuItemId;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Long getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(Long menuItemId) {
        this.menuItemId = menuItemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
