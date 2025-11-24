package com.example.Bhoklagyo.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer extends User {
    
    private String address;
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();
    
    public Customer() {}
    
    public Customer(String username, String name, String password, String email, String phoneNumber, String address) {
        this.setUsername(username);
        this.setName(name);
        this.setPassword(password);
        this.setEmail(email);
        this.setPhoneNumber(phoneNumber);
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
