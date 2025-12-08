package com.example.Bhoklagyo.websocket;

import com.example.Bhoklagyo.dto.OrderResponse;
import com.example.Bhoklagyo.entity.Order;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventSender {
    private final SimpMessagingTemplate messagingTemplate;

    public OrderEventSender(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Send order notification to customer
     * Topic: /topic/orders/customer/{customerId}
     */
    public void sendOrderToCustomer(Long customerId, OrderResponse payload) {
        String topic = "/topic/orders/customer/" + customerId;
        messagingTemplate.convertAndSend(topic, payload);
        System.out.println("📤 WebSocket: Sent order notification to customer topic: " + topic);
    }

    /**
     * Send order notification to restaurant
     * Topic: /topic/orders/restaurant/{restaurantId}
     */
    public void sendOrderToRestaurant(Long restaurantId, OrderResponse payload) {
        String topic = "/topic/orders/restaurant/" + restaurantId;
        messagingTemplate.convertAndSend(topic, payload);
        System.out.println("📤 WebSocket: Sent order notification to restaurant topic: " + topic);
    }

    

}

