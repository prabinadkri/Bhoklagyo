package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.NotificationResponse;
import com.example.Bhoklagyo.entity.Notification;

import java.util.List;

public interface NotificationService {
    Notification save(Notification notification);
    List<NotificationResponse> getNotificationsForUser(Long userId);
    List<NotificationResponse> getNotificationsForRestaurant(Long restaurantId);
    void markAsRead(Long notificationId);
    void markAllAsReadForUser(Long userId);
    void markAllAsReadForRestaurant(Long restaurantId);
    void deleteAllForUser(Long userId);
    void deleteAllForRestaurant(Long restaurantId);
}
