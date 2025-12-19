package com.example.Bhoklagyo.dto;

import java.time.LocalDateTime;

public class NotificationResponse {
    private Long id;
    private Long orderId;
    private Long restaurantId;
    private String type;
    private LocalDateTime createdAt;
    private String message;
    private Long userId;
    private Boolean isRead;

    public NotificationResponse() {}

    public NotificationResponse(Long id, Long orderId, Long restaurantId, String type, LocalDateTime createdAt, String message, Long userId, Boolean isRead) {
        this.id = id;
        this.orderId = orderId;
        this.restaurantId = restaurantId;
        this.type = type;
        this.createdAt = createdAt;
        this.message = message;
        this.userId = userId;
        this.isRead = isRead;
    }

    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
    public Long getRestaurantId() { return restaurantId; }
    public String getType() { return type; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getMessage() { return message; }
    public Long getUserId() { return userId; }
    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
}
