package com.example.Bhoklagyo.dto;

public class MenuItemResponse {
    private Long id;
    private String name;
    private String desc;
    private Double price;
    private Long restaurantId;

    public MenuItemResponse() {}

    public MenuItemResponse(Long id, String name, String desc, Double price, Long restaurantId) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.price = price;
        this.restaurantId = restaurantId;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }
}
