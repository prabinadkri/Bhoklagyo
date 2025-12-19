package com.example.Bhoklagyo.websocket;

import com.example.Bhoklagyo.entity.Restaurant;
import com.example.Bhoklagyo.repository.RestaurantRepository;
import com.example.Bhoklagyo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Optional;

@Component
public class WebSocketSubscriptionInterceptor implements ChannelInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketSubscriptionInterceptor.class);

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public WebSocketSubscriptionInterceptor(RestaurantRepository restaurantRepository, UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Message<?> preSend(Message<?> message, org.springframework.messaging.MessageChannel channel) {
        SimpMessageHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, SimpMessageHeaderAccessor.class);
        if (accessor == null) return message;

        if (SimpMessageType.SUBSCRIBE.equals(accessor.getMessageType())) {
            String destination = accessor.getDestination();
            Principal user = accessor.getUser();
            if (destination != null && destination.startsWith("/topic/restaurant.")) {
                // Destination pattern: /topic/restaurant.{restaurantId}
                try {
                    String[] parts = destination.split("\\.");
                    String idPart = parts[parts.length - 1];
                    Long restaurantId = Long.parseLong(idPart);

                    Optional<Restaurant> opt = restaurantRepository.findById(restaurantId);
                    if (opt.isEmpty()) {
                        logger.warn("Subscription to non-existent restaurant topic: {}", restaurantId);
                        return null; // reject subscription
                    }

                    Restaurant restaurant = opt.get();
                    if (restaurant.getOwner() == null) {
                        logger.warn("Restaurant {} has no owner, rejecting subscription", restaurantId);
                        return null;
                    }

                    String ownerEmail = restaurant.getOwner().getEmail();
                    String principalName = user != null ? user.getName() : null;

                    // Allow if subscriber is the owner or has ADMIN role (authority check via Authentication)
                    if (principalName == null || !(principalName.equals(ownerEmail) || hasAdminRole(accessor))) {
                        logger.warn("User {} not authorized to subscribe to restaurant {} topic", principalName, restaurantId);
                        return null;
                    }
                } catch (Exception ex) {
                    logger.error("Error validating subscription destination {}", destination, ex);
                    return null;
                }
            }
        }

        return message;
    }

    private boolean hasAdminRole(SimpMessageHeaderAccessor accessor) {
        Authentication auth = (Authentication) accessor.getUser();
        if (auth == null) return false;
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
