package com.example.Bhoklagyo.event;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Domain event published when an order is created, updated, or cancelled.
 * Consumed by notification and analytics services.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderEvent {

    public enum EventType {
        ORDER_CREATED,
        ORDER_STATUS_UPDATED,
        ORDER_CANCELLED,
        ORDER_FEEDBACK_SUBMITTED
    }

    private String eventId;
    private EventType eventType;
    private Long orderId;
    private Long restaurantId;
    private Long customerId;
    private String status;
    private Double totalPrice;
    private String feedback;
    private Integer rating;
    private List<OrderItemEvent> items;
    private LocalDateTime timestamp;
    private String correlationId;  // X-Request-ID for tracing

    public OrderEvent() {
        this.timestamp = LocalDateTime.now();
    }

    public OrderEvent(EventType eventType, Long orderId, Long restaurantId,
                      Long customerId, String status, Double totalPrice,
                      String correlationId) {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.eventType = eventType;
        this.orderId = orderId;
        this.restaurantId = restaurantId;
        this.customerId = customerId;
        this.status = status;
        this.totalPrice = totalPrice;
        this.correlationId = correlationId;
        this.timestamp = LocalDateTime.now();
    }

    // --- Getters & Setters ---

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public EventType getEventType() { return eventType; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public List<OrderItemEvent> getItems() { return items; }
    public void setItems(List<OrderItemEvent> items) { this.items = items; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }

    @Override
    public String toString() {
        return "OrderEvent{eventId='" + eventId + "', type=" + eventType +
                ", orderId=" + orderId + ", restaurant=" + restaurantId +
                ", customer=" + customerId + ", status='" + status + "'}";
    }
}
