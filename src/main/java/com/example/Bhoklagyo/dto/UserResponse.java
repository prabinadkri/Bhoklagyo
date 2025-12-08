package com.example.Bhoklagyo.dto;

import com.example.Bhoklagyo.entity.Role;

public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private Role role;
    private String address;

    public UserResponse() {}

    public UserResponse(Long id, String name, String email, String phoneNumber, Role role, String address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.address = address;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
