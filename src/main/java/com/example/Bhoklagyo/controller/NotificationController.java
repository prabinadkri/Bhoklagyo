package com.example.Bhoklagyo.controller;

import com.example.Bhoklagyo.dto.NotificationResponse;
import com.example.Bhoklagyo.entity.Restaurant;
import com.example.Bhoklagyo.entity.Role;
import com.example.Bhoklagyo.entity.User;
import com.example.Bhoklagyo.exception.ResourceNotFoundException;
import com.example.Bhoklagyo.repository.RestaurantRepository;
import com.example.Bhoklagyo.repository.UserRepository;
import com.example.Bhoklagyo.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    public NotificationController(NotificationService notificationService, UserRepository userRepository, RestaurantRepository restaurantRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @GetMapping("/notifications/user")
    public ResponseEntity<List<NotificationResponse>> getMyNotifications() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        List<NotificationResponse> list = notificationService.getNotificationsForUser(currentUser.getId());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/notifications/restaurant/{restaurantId}")
    public ResponseEntity<List<NotificationResponse>> getRestaurantNotifications(@PathVariable Long restaurantId) {
        // authorization: only owner or employed employee may view
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));

        boolean isOwner = restaurant.getOwner() != null && restaurant.getOwner().getId().equals(currentUser.getId());
        boolean isEmployee = currentUser.getRole() == Role.EMPLOYEE && currentUser.getEmployedRestaurant() != null && currentUser.getEmployedRestaurant().getId().equals(restaurantId);

        if (!isOwner && !isEmployee) {
            throw new AccessDeniedException("You are not authorized to view notifications for this restaurant");
        }

        List<NotificationResponse> list = notificationService.getNotificationsForRestaurant(restaurantId);
        return ResponseEntity.ok(list);
    }

    @PatchMapping("/notifications/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/notifications/user/read-all")
    public ResponseEntity<Void> markAllUserNotificationsRead() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        notificationService.markAllAsReadForUser(currentUser.getId());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/notifications/restaurant/{restaurantId}/read-all")
    public ResponseEntity<Void> markAllRestaurantNotificationsRead(@PathVariable Long restaurantId) {
        // authorization: only owner or employed employee may perform
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));

        boolean isOwner = restaurant.getOwner() != null && restaurant.getOwner().getId().equals(currentUser.getId());
        boolean isEmployee = currentUser.getRole() == Role.EMPLOYEE && currentUser.getEmployedRestaurant() != null && currentUser.getEmployedRestaurant().getId().equals(restaurantId);

        if (!isOwner && !isEmployee) {
            throw new AccessDeniedException("You are not authorized to mark notifications for this restaurant");
        }

        notificationService.markAllAsReadForRestaurant(restaurantId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/notifications/user")
    public ResponseEntity<Void> deleteAllUserNotifications() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        notificationService.deleteAllForUser(currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/notifications/restaurant/{restaurantId}")
    public ResponseEntity<Void> deleteAllRestaurantNotifications(@PathVariable Long restaurantId) {
        // authorization: only owner or employed employee may perform
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));

        boolean isOwner = restaurant.getOwner() != null && restaurant.getOwner().getId().equals(currentUser.getId());
        boolean isEmployee = currentUser.getRole() == Role.EMPLOYEE && currentUser.getEmployedRestaurant() != null && currentUser.getEmployedRestaurant().getId().equals(restaurantId);

        if (!isOwner && !isEmployee) {
            throw new AccessDeniedException("You are not authorized to delete notifications for this restaurant");
        }

        notificationService.deleteAllForRestaurant(restaurantId);
        return ResponseEntity.noContent().build();
    }
}
