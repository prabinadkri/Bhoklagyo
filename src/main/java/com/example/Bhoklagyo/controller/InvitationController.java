package com.example.Bhoklagyo.controller;

import com.example.Bhoklagyo.entity.Restaurant;
import com.example.Bhoklagyo.entity.User;
import com.example.Bhoklagyo.repository.RestaurantRepository;
import com.example.Bhoklagyo.repository.UserRepository;
import com.example.Bhoklagyo.security.InviteTokenUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/invitations")
public class InvitationController {

    private final InviteTokenUtil inviteTokenUtil;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    public InvitationController(InviteTokenUtil inviteTokenUtil,
                                UserRepository userRepository,
                                RestaurantRepository restaurantRepository) {
        this.inviteTokenUtil = inviteTokenUtil;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @GetMapping("/accept")
    public ResponseEntity<?> acceptInvitation(@RequestParam("token") String token,
                                              Authentication authentication) {
        if (!inviteTokenUtil.isInviteToken(token)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid token type"));
        }
        if (inviteTokenUtil.isExpired(token)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Token expired"));
        }

        String email = inviteTokenUtil.getInviteeEmail(token);
        Long restaurantId = inviteTokenUtil.getRestaurantId(token);
        Long ownerId = inviteTokenUtil.getOwnerId(token);

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        if (restaurant.getOwner() == null || !restaurant.getOwner().getId().equals(ownerId)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid restaurant owner"));
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User with email not found"));
        }

        if (authentication == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
        }
        String authEmail = authentication.getName();
        User authUser = userRepository.findByEmail(authEmail).orElse(null);
        if (authUser == null || authUser.getId() == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid authenticated user"));
        }
        if (!authUser.getEmail().equalsIgnoreCase(email)) {
            return ResponseEntity.status(403).body(Map.of("error", "Forbidden: must accept as invited user"));
        }

        user.setEmployedRestaurant(restaurant);
        user.setRole(com.example.Bhoklagyo.entity.Role.EMPLOYEE);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Invitation accepted. You are now an EMPLOYEE.",
                "restaurantId", restaurant.getId()));
    }
}
