package com.example.Bhoklagyo.dto;

public class RestaurantRequest {
    private String name;

    public RestaurantRequest() {}

    public RestaurantRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
