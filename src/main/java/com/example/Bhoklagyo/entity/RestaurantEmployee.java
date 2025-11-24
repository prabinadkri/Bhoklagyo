package com.example.Bhoklagyo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "restaurant_employees")
public class RestaurantEmployee extends User {
    
    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;
    
    public RestaurantEmployee() {}
    
    public RestaurantEmployee(String username, String name, String password, String email, String phoneNumber, Restaurant restaurant) {
        this.setUsername(username);
        this.setName(name);
        this.setPassword(password);
        this.setEmail(email);
        this.setPhoneNumber(phoneNumber);
        this.restaurant = restaurant;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
}
