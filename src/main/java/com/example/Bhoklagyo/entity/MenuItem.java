package com.example.Bhoklagyo.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "menu_items")
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
    @OneToMany(mappedBy = "menuItem", cascade = CascadeType.ALL)
    private List<RestaurantMenuItem> restaurantMenuItems = new ArrayList<>();

    public MenuItem() {}
    
    public MenuItem(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
