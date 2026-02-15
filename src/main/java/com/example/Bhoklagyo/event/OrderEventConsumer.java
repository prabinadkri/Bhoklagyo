package com.example.Bhoklagyo.event;

import com.example.Bhoklagyo.config.KafkaConfig;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Consumes order events from Kafka for analytics, metrics, and audit logging.
 * In a microservices architecture, this would live in a separate analytics service.
 */
@Component
@ConditionalOnBean(KafkaTemplate.class)
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);

    private final Counter orderCreatedCounter;
    private final Counter orderStatusUpdatedCounter;
    private final Counter orderCancelledCounter;

    public OrderEventConsumer(MeterRegistry meterRegistry) {
        this.orderCreatedCounter = Counter.builder("bhoklagyo.orders.created")
                .description("Total orders created")
                .register(meterRegistry);
        this.orderStatusUpdatedCounter = Counter.builder("bhoklagyo.orders.status_updated")
                .description("Total order status updates")
                .register(meterRegistry);
        this.orderCancelledCounter = Counter.builder("bhoklagyo.orders.cancelled")
                .description("Total orders cancelled")
                .register(meterRegistry);
    }

    @KafkaListener(
            topics = KafkaConfig.TOPIC_ORDER_EVENTS,
            containerFactory = "orderKafkaListenerContainerFactory",
            groupId = "${spring.kafka.consumer.group-id:bhoklagyo-group}"
    )
    public void handleOrderEvent(OrderEvent event) {
        // Restore correlation ID into MDC for log tracing
        if (event.getCorrelationId() != null) {
            MDC.put("requestId", event.getCorrelationId());
        }

        try {
            log.info("Consumed order event: type={}, orderId={}, restaurant={}, customer={}, status={}",
                    event.getEventType(), event.getOrderId(), event.getRestaurantId(),
                    event.getCustomerId(), event.getStatus());

            switch (event.getEventType()) {
                case ORDER_CREATED -> {
                    orderCreatedCounter.increment();
                    log.info("Order #{} created for restaurant #{}, total={}",
                            event.getOrderId(), event.getRestaurantId(), event.getTotalPrice());
                }
                case ORDER_STATUS_UPDATED -> {
                    orderStatusUpdatedCounter.increment();
                    log.info("Order #{} status changed to '{}'",
                            event.getOrderId(), event.getStatus());
                }
                case ORDER_CANCELLED -> {
                    orderCancelledCounter.increment();
                    log.info("Order #{} cancelled", event.getOrderId());
                }
                case ORDER_FEEDBACK_SUBMITTED -> {
                    log.info("Order #{} feedback: rating={}, feedback='{}'",
                            event.getOrderId(), event.getRating(), event.getFeedback());
                }
            }
        } finally {
            MDC.remove("requestId");
        }
    }
}
