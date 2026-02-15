package com.example.Bhoklagyo.websocket;

import com.example.Bhoklagyo.dto.OrderResponse;
import com.example.Bhoklagyo.entity.Notification;
import com.example.Bhoklagyo.entity.NotificationType;
import com.example.Bhoklagyo.service.NotificationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OrderEventSender {
    private static final Logger log = LoggerFactory.getLogger(OrderEventSender.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    public OrderEventSender(SimpMessagingTemplate messagingTemplate, NotificationService notificationService) {
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
    }

    /**
     * Send order notification to customer
     * Topic: /topic/orders/customer/{customerId}
     */
    public void sendOrderToCustomer(Long customerId, OrderResponse payload) {
        String topic = "/topic/orders/customer/" + customerId;
        messagingTemplate.convertAndSend(topic, payload);
        log.debug("WebSocket: Sent order notification to customer topic: {}", topic);

        // persist notification for the user
        try {
            Notification n = new Notification(payload.getId(), payload.getRestaurantId(), NotificationType.ORDER_STATUS_CHANGED, LocalDateTime.now(), payload.getStatus() + " - Order update", customerId);
            notificationService.save(n);
        } catch (Exception e) {
            log.warn("Failed to persist notification for customer {}: {}", customerId, e.getMessage());
        }
    }

    /**
     * Send order notification to restaurant
     * Topic: /topic/orders/restaurant/{restaurantId}
     */
    public void sendOrderToRestaurant(Long restaurantId, OrderResponse payload) {
        String topic = "/topic/orders/restaurant/" + restaurantId;
        messagingTemplate.convertAndSend(topic, payload);
        log.debug("WebSocket: Sent order notification to restaurant topic: {}", topic);

        // persist notification for the restaurant (no specific userId)
        try {
            Notification n = new Notification(payload.getId(), restaurantId, NotificationType.ORDER_STATUS_CHANGED, LocalDateTime.now(), payload.getStatus() + " - Order update", null);
            notificationService.save(n);
        } catch (Exception e) {
            log.warn("Failed to persist notification for restaurant {}: {}", restaurantId, e.getMessage());
        }
    }

    /**
     * Send an ORDER_CREATED notification to the restaurant (used at order creation)
     */
    public void sendOrderCreatedToRestaurant(Long restaurantId, OrderResponse payload) {
        String topic = "/topic/orders/restaurant/" + restaurantId;
        messagingTemplate.convertAndSend(topic, payload);
        log.debug("WebSocket: Sent order CREATED notification to restaurant topic: {}", topic);

        // persist notification for the restaurant (no specific userId)
        try {
            Notification n = new Notification(payload.getId(), restaurantId, NotificationType.ORDER_CREATED, LocalDateTime.now(), payload.getStatus() + " - Order created", null);
            notificationService.save(n);
        } catch (Exception e) {
            log.warn("Failed to persist created-notification for restaurant {}: {}", restaurantId, e.getMessage());
        }
    }

}
