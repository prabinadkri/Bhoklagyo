package com.example.Bhoklagyo.dto;

import java.util.List;

public class RestaurantResponse {
    private Long id;
    private String name;
    private Double latitude;
    private Double longitude;
    private Boolean isFeatured;
    private String photoUrl;
    private List<String> cuisineTags;
    private List<String> dietaryTags;

    public RestaurantResponse() {}

    public RestaurantResponse(Long id, String name, Double latitude, Double longitude, Boolean isFeatured, String photoUrl, List<String> cuisineTags, List<String> dietaryTags) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isFeatured = isFeatured;
        this.photoUrl = photoUrl;
        this.cuisineTags = cuisineTags;
        this.dietaryTags = dietaryTags;
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public List<String> getCuisineTags() {
        return cuisineTags;
    }

    public void setCuisineTags(List<String> cuisineTags) {
        this.cuisineTags = cuisineTags;
    }

    public List<String> getDietaryTags() {
        return dietaryTags;
    }

    public void setDietaryTags(List<String> dietaryTags) {
        this.dietaryTags = dietaryTags;
    }
}
