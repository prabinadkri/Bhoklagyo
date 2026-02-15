package com.example.Bhoklagyo.event;

import com.example.Bhoklagyo.config.KafkaConfig;
import com.example.Bhoklagyo.entity.Notification;
import com.example.Bhoklagyo.entity.NotificationType;
import com.example.Bhoklagyo.service.NotificationService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Consumes notification events from Kafka and:
 * 1. Persists them to the database
 * 2. Pushes real-time updates via WebSocket/STOMP
 *
 * In a microservices architecture, this would be a standalone notification service.
 */
@Component
@ConditionalOnBean(KafkaTemplate.class)
public class NotificationEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventConsumer.class);

    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;
    private final Counter notificationsSentCounter;

    public NotificationEventConsumer(NotificationService notificationService,
                                     SimpMessagingTemplate messagingTemplate,
                                     MeterRegistry meterRegistry) {
        this.notificationService = notificationService;
        this.messagingTemplate = messagingTemplate;
        this.notificationsSentCounter = Counter.builder("bhoklagyo.notifications.sent")
                .description("Total notifications processed")
                .register(meterRegistry);
    }

    @KafkaListener(
            topics = KafkaConfig.TOPIC_NOTIFICATION_EVENTS,
            containerFactory = "notificationKafkaListenerContainerFactory",
            groupId = "${spring.kafka.consumer.group-id:bhoklagyo-group}-notifications"
    )
    public void handleNotificationEvent(NotificationEvent event) {
        if (event.getCorrelationId() != null) {
            MDC.put("requestId", event.getCorrelationId());
        }

        try {
            log.info("Consumed notification event: type={}, orderId={}, userId={}, channel={}",
                    event.getNotificationType(), event.getOrderId(),
                    event.getUserId(), event.getChannel());

            // 1. Persist notification to database
            persistNotification(event);

            // 2. Send real-time WebSocket push
            sendWebSocketNotification(event);

            notificationsSentCounter.increment();

        } catch (Exception e) {
            log.error("Failed to process notification event {}: {}",
                    event.getEventId(), e.getMessage(), e);
            throw e; // Rethrow so DLT handler catches it
        } finally {
            MDC.remove("requestId");
        }
    }

    private void persistNotification(NotificationEvent event) {
        try {
            NotificationType type = mapNotificationType(event.getNotificationType());
            Notification notification = new Notification(
                    event.getOrderId(),
                    event.getRestaurantId(),
                    type,
                    LocalDateTime.now(),
                    event.getMessage(),
                    event.getUserId()
            );
            notificationService.save(notification);
            log.debug("Persisted notification for orderId={}, userId={}",
                    event.getOrderId(), event.getUserId());
        } catch (Exception e) {
            log.error("Failed to persist notification: {}", e.getMessage());
            // Don't rethrow — still try to send WebSocket
        }
    }

    private void sendWebSocketNotification(NotificationEvent event) {
        try {
            Map<String, Object> payload = Map.of(
                    "orderId", event.getOrderId() != null ? event.getOrderId() : 0,
                    "restaurantId", event.getRestaurantId() != null ? event.getRestaurantId() : 0,
                    "type", event.getNotificationType(),
                    "message", event.getMessage() != null ? event.getMessage() : "",
                    "timestamp", event.getTimestamp().toString()
            );

            // Send to customer topic
            if (event.getUserId() != null) {
                String customerTopic = "/topic/orders/customer/" + event.getUserId();
                messagingTemplate.convertAndSend(customerTopic, payload);
                log.debug("WebSocket sent to {}", customerTopic);
            }

            // Send to restaurant topic
            if (event.getRestaurantId() != null) {
                String restaurantTopic = "/topic/orders/restaurant/" + event.getRestaurantId();
                messagingTemplate.convertAndSend(restaurantTopic, payload);
                log.debug("WebSocket sent to {}", restaurantTopic);
            }
        } catch (Exception e) {
            log.error("Failed to send WebSocket notification: {}", e.getMessage());
        }
    }

    private NotificationType mapNotificationType(String type) {
        try {
            return NotificationType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return NotificationType.ORDER_STATUS_CHANGED;
        }
    }
}
