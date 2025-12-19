package com.example.Bhoklagyo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private Long restaurantId;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(columnDefinition = "timestamp")
    private LocalDateTime createdAt;

    @Column(length = 1000)
    private String message;

    private Long userId; // recipient user id (customer)

    private Boolean isRead = false;

    public Notification() {}

    public Notification(Long orderId, Long restaurantId, NotificationType type, LocalDateTime createdAt, String message, Long userId) {
        this.orderId = orderId;
        this.restaurantId = restaurantId;
        this.type = type;
        this.createdAt = createdAt;
        this.message = message;
        this.userId = userId;
        this.isRead = false;
    }

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean read) {
        isRead = read;
    }
}
