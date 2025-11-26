package com.example.Bhoklagyo.dto;

public class AssignOwnerRequest {
    private Long userId;
    private Long restaurantId;

    public AssignOwnerRequest() {}

    public AssignOwnerRequest(Long userId, Long restaurantId) {
        this.userId = userId;
        this.restaurantId = restaurantId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }
}
