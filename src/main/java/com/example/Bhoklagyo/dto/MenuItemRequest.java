package com.example.Bhoklagyo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class MenuItemRequest {
    private Long categoryId;  // Reference to base Category, null if creating new
    private String categoryName;  // For creating new base Category (e.g., "Pizza", "Burger")

    @NotBlank(message = "Menu item name is required")
    @Size(max = 200, message = "Name must not exceed 200 characters")
    private String name;      // Restaurant-specific name (e.g., "Margherita Pizza", "Cheese Burger")

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Double price;

    @Positive(message = "Discounted price must be positive")
    private Double discountedPrice;
    private Boolean isVegan;
    private Boolean isVegetarian;
    private String allergyWarnings;
    private Boolean isTodaySpecial;
    private Boolean available;

    public MenuItemRequest() {}

    public MenuItemRequest(Long categoryId, String categoryName, String name, String description, Double price, Double discountedPrice, Boolean isVegan, Boolean isVegetarian, String allergyWarnings, Boolean isTodaySpecial, Boolean available) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.name = name;
        this.description = description;
        this.price = price;
        this.discountedPrice = discountedPrice;
        this.isVegan = isVegan;
        this.isVegetarian = isVegetarian;
        this.allergyWarnings = allergyWarnings;
        this.isTodaySpecial = isTodaySpecial;
        this.available = available;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public Double getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(Double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public Boolean getIsVegan() {
        return isVegan;
    }

    public void setIsVegan(Boolean isVegan) {
        this.isVegan = isVegan;
    }

    public Boolean getIsVegetarian() {
        return isVegetarian;
    }

    public void setIsVegetarian(Boolean isVegetarian) {
        this.isVegetarian = isVegetarian;
    }

    public String getAllergyWarnings() {
        return allergyWarnings;
    }

    public void setAllergyWarnings(String allergyWarnings) {
        this.allergyWarnings = allergyWarnings;
    }

    public Boolean getIsTodaySpecial() {
        return isTodaySpecial;
    }

    public void setIsTodaySpecial(Boolean isTodaySpecial) {
        this.isTodaySpecial = isTodaySpecial;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
}
