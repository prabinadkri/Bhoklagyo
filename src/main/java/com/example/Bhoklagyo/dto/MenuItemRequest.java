package com.example.Bhoklagyo.dto;

public class MenuItemRequest {
    private Long categoryId;  // Reference to base Category, null if creating new
    private String categoryName;  // For creating new base Category (e.g., "Pizza", "Burger")
    private String name;      // Restaurant-specific name (e.g., "Margherita Pizza", "Cheese Burger")
    private String description;
    private Double price;
    private Boolean isVegan;
    private Boolean isVegetarian;
    private String allergyWarnings;
    private Boolean isTodaySpecial;

    public MenuItemRequest() {}

    public MenuItemRequest(Long categoryId, String categoryName, String name, String description, Double price, Boolean isVegan, Boolean isVegetarian, String allergyWarnings, Boolean isTodaySpecial) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.name = name;
        this.description = description;
        this.price = price;
        this.isVegan = isVegan;
        this.isVegetarian = isVegetarian;
        this.allergyWarnings = allergyWarnings;
        this.isTodaySpecial = isTodaySpecial;
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
}
