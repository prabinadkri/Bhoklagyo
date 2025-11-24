package com.example.Bhoklagyo.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurant_owners")
public class RestaurantOwner extends User {
    
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Restaurant> restaurants = new ArrayList<>();
    
    public RestaurantOwner() {}
    
    public RestaurantOwner(String username, String name, String password, String email, String phoneNumber) {
        this.setUsername(username);
        this.setName(name);
        this.setPassword(password);
        this.setEmail(email);
        this.setPhoneNumber(phoneNumber);
    }

    public List<Restaurant> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }
}
