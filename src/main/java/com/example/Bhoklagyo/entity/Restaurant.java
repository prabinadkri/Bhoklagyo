package com.example.Bhoklagyo.entity;
import java.util.*;

import jakarta.persistence.*;

@Entity
@Table(name = "restaurants")
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<RestaurantMenuItem> restaurantMenuItems = new ArrayList<>();
    
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private RestaurantOwner owner;


    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<RestaurantMenuItem> getRestaurantMenuItems() {
        return restaurantMenuItems;
    }
    public void setRestaurantMenuItems(List<RestaurantMenuItem> restaurantMenuItems) {
        this.restaurantMenuItems = restaurantMenuItems;
    }
    public RestaurantOwner getOwner() {
        return owner;
    }
    public void setOwner(RestaurantOwner owner) {
        this.owner = owner;
    }
}