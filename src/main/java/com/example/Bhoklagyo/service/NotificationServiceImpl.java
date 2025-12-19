package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.NotificationResponse;
import com.example.Bhoklagyo.entity.Notification;
import com.example.Bhoklagyo.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsForUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(n -> new NotificationResponse(n.getId(), n.getOrderId(), n.getRestaurantId(), n.getType() == null ? null : n.getType().name(), n.getCreatedAt(), n.getMessage(), n.getUserId(), n.getIsRead()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsForRestaurant(Long restaurantId) {
        return notificationRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId)
                .stream()
                .map(n -> new NotificationResponse(n.getId(), n.getOrderId(), n.getRestaurantId(), n.getType() == null ? null : n.getType().name(), n.getCreatedAt(), n.getMessage(), n.getUserId(), n.getIsRead()))
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setIsRead(true);
            notificationRepository.save(n);
        });
    }

    @Override
    public void markAllAsReadForUser(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }

    @Override
    public void markAllAsReadForRestaurant(Long restaurantId) {
        notificationRepository.markAllAsReadByRestaurantId(restaurantId);
    }

    @Override
    public void deleteAllForUser(Long userId) {
        notificationRepository.deleteAllByUserId(userId);
    }

    @Override
    public void deleteAllForRestaurant(Long restaurantId) {
        notificationRepository.deleteAllByRestaurantId(restaurantId);
    }
}
