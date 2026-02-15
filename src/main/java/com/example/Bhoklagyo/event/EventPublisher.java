package com.example.Bhoklagyo.event;

import com.example.Bhoklagyo.config.KafkaConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Centralized Kafka event publisher.
 * All domain events flow through this service.
 * Fail-open: if Kafka is unavailable, log the error and continue.
 */
@Service
@ConditionalOnBean(KafkaTemplate.class)
public class EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(EventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publish an order event, keyed by orderId for partition ordering.
     */
    public void publishOrderEvent(OrderEvent event) {
        // Inject correlation ID from MDC if not already set
        if (event.getCorrelationId() == null) {
            event.setCorrelationId(MDC.get("requestId"));
        }

        String key = String.valueOf(event.getOrderId());
        publish(KafkaConfig.TOPIC_ORDER_EVENTS, key, event);
    }

    /**
     * Publish a notification event, keyed by userId (or restaurantId if no user).
     */
    public void publishNotificationEvent(NotificationEvent event) {
        if (event.getCorrelationId() == null) {
            event.setCorrelationId(MDC.get("requestId"));
        }

        String key = event.getUserId() != null
                ? "user-" + event.getUserId()
                : "restaurant-" + event.getRestaurantId();
        publish(KafkaConfig.TOPIC_NOTIFICATION_EVENTS, key, event);
    }

    private void publish(String topic, String key, Object event) {
        try {
            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(topic, key, event);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to publish event to topic={}, key={}: {}",
                            topic, key, ex.getMessage(), ex);
                } else {
                    log.debug("Published event to topic={}, partition={}, offset={}, key={}",
                            topic,
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset(),
                            key);
                }
            });
        } catch (Exception e) {
            // Fail-open: don't break the business flow if Kafka is down
            log.error("Kafka unavailable, event dropped: topic={}, key={}, error={}",
                    topic, key, e.getMessage());
        }
    }
}
