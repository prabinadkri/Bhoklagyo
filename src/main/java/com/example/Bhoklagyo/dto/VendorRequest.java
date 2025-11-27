package com.example.Bhoklagyo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

public class VendorRequest {
    
    @NotBlank(message = "PAN number is required")
    private String panNumber;
    
    @NotBlank(message = "Business name is required")
    private String businessName;
    
    private String accountNumber;
    
    private Boolean isVatRegistered = false;
    
    @Email(message = "Invalid email format")
    private String email;
    
    private String phoneNumber;
    
    private String address;

    // Getters and Setters
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
}
