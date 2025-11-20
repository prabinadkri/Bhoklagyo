package com.example.Bhoklagyo.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "restaurant_menu_items",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_restaurant_menuitem",
        columnNames = {"restaurant_id", "menu_item_id"}
    )
)
public class RestaurantMenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;
    
    @ManyToOne
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;
    
    @Column(name = "description")
    private String description;
    
    @Column(nullable = false)
    private Double price;
    
    private Boolean available = true;

    public RestaurantMenuItem() {}
    
    public RestaurantMenuItem(Restaurant restaurant, MenuItem menuItem, String description, Double price) {
        this.restaurant = restaurant;
        this.menuItem = menuItem;
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

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
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
}
