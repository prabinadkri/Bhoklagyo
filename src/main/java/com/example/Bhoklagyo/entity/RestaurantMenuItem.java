package com.example.Bhoklagyo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "restaurant_menu_items")
public class RestaurantMenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;
    
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @Column(columnDefinition = "varchar(255) default ''")
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Column(nullable = false)
    private Double price;
    
    private Boolean available = true;
    
    private Boolean isVegan = false;
    
    private Boolean isVegetarian = false;
    
    @Column(length = 500)
    private String allergyWarnings;
    
    private Boolean isTodaySpecial = false;

    public RestaurantMenuItem() {}
    
    public RestaurantMenuItem(Restaurant restaurant, Category category, String name, String description, Double price) {
        this.restaurant = restaurant;
        this.category = category;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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
