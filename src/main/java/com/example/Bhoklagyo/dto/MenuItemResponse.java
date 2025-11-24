package com.example.Bhoklagyo.dto;

public class MenuItemResponse {
    private Long id;              // RestaurantMenuItem ID
    private Long categoryId;      // Base Category ID
    private String categoryName;  // Category name
    private String name;          // Restaurant-specific name
    private String description;
    private Double price;
    private Long restaurantId;
    private Boolean available;
    private Boolean isVegan;
    private Boolean isVegetarian;
    private String allergyWarnings;
    private Boolean isTodaySpecial;

    public MenuItemResponse() {}

    public MenuItemResponse(Long id, Long categoryId, String categoryName, String name, String description, Double price, Long restaurantId, Boolean available, Boolean isVegan, Boolean isVegetarian, String allergyWarnings, Boolean isTodaySpecial) {
        this.id = id;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.name = name;
        this.description = description;
        this.price = price;
        this.restaurantId = restaurantId;
        this.available = available;
        this.isVegan = isVegan;
        this.isVegetarian = isVegetarian;
        this.allergyWarnings = allergyWarnings;
        this.isTodaySpecial = isTodaySpecial;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
