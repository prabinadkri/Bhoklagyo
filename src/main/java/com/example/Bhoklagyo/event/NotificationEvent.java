package com.example.Bhoklagyo.event;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * Domain event published when a notification needs to be sent.
 * Consumed by notification delivery service (email, push, WebSocket).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationEvent {

    public enum Channel {
        WEBSOCKET,
        EMAIL,
        PUSH
    }

    private String eventId;
    private Long orderId;
    private Long restaurantId;
    private Long userId;
    private String notificationType;  // ORDER_CREATED, ORDER_STATUS_CHANGED
    private String message;
    private Channel channel;
    private LocalDateTime timestamp;
    private String correlationId;

    public NotificationEvent() {
        this.timestamp = LocalDateTime.now();
    }

    public NotificationEvent(Long orderId, Long restaurantId, Long userId,
                             String notificationType, String message,
                             Channel channel, String correlationId) {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.orderId = orderId;
        this.restaurantId = restaurantId;
        this.userId = userId;
        this.notificationType = notificationType;
        this.message = message;
        this.channel = channel;
        this.correlationId = correlationId;
        this.timestamp = LocalDateTime.now();
    }

    // --- Getters & Setters ---

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getNotificationType() { return notificationType; }
    public void setNotificationType(String notificationType) { this.notificationType = notificationType; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Channel getChannel() { return channel; }
    public void setChannel(Channel channel) { this.channel = channel; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }

    @Override
    public String toString() {
        return "NotificationEvent{eventId='" + eventId + "', type='" + notificationType +
                "', orderId=" + orderId + ", userId=" + userId + ", channel=" + channel + "}";
    }
}
