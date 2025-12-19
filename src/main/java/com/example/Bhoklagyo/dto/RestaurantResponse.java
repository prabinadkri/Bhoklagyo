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
    private String addressLabel;
    private Double rating;
    private String openingTime;
    private String closingTime;
    private Boolean isOpen;
    private long totalCount;
    private int averageForOne;

    public RestaurantResponse() {}

    public RestaurantResponse(Long id, String name, Double latitude, Double longitude, Boolean isFeatured, String photoUrl, List<String> cuisineTags, List<String> dietaryTags, Double rating, String openingTime, String closingTime, Boolean isOpen, String addressLabel, long totalCount,int averageForOne) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isFeatured = isFeatured;
        this.photoUrl = photoUrl;
        this.cuisineTags = cuisineTags;
        this.dietaryTags = dietaryTags;
        this.rating = rating;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.isOpen = isOpen;
        this.addressLabel = addressLabel;
        this.totalCount = totalCount;
        this.averageForOne = averageForOne;
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

    public String getAddressLabel() {
        return addressLabel;
    }

    public void setAddressLabel(String addressLabel) {
        this.addressLabel = addressLabel;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }

    public Boolean getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Boolean isOpen) {
        this.isOpen = isOpen;
    }
    public long getTotalCount() {
        return totalCount;
    }
    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }
    public int getAverageForOne() {return averageForOne;}
    public void setAverageForOne(int averageForOne) {this.averageForOne = averageForOne;}
}
