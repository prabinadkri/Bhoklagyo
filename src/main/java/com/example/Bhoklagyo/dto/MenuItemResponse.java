package com.example.Bhoklagyo.dto;

public class MenuItemResponse {
    private Long id;              // RestaurantMenuItem ID
    private Long menuItemId;      // Base MenuItem ID
    private String name;
    private String description;
    private Double price;
    private Long restaurantId;
    private Boolean available;

    public MenuItemResponse() {}

    public MenuItemResponse(Long id, Long menuItemId, String name, String description, Double price, Long restaurantId, Boolean available) {
        this.id = id;
        this.menuItemId = menuItemId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.restaurantId = restaurantId;
        this.available = available;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
}
