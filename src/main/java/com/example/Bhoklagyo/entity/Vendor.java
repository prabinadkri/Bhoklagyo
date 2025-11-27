package com.example.Bhoklagyo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vendors")
public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pan_number", unique = true, nullable = false)
    private String panNumber;

    @Column(name = "business_name", nullable = false)
    private String businessName;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "is_vat_registered", nullable = false)
    private Boolean isVatRegistered = false;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Restaurant> restaurants = new ArrayList<>();

    public Vendor() {}

    public Vendor(String panNumber, String businessName, String accountNumber, Boolean isVatRegistered) {
        this.panNumber = panNumber;
        this.businessName = businessName;
        this.accountNumber = accountNumber;
        this.isVatRegistered = isVatRegistered;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Boolean getIsVatRegistered() {
        return isVatRegistered;
    }

    public void setIsVatRegistered(Boolean isVatRegistered) {
        this.isVatRegistered = isVatRegistered;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Restaurant> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }
}
