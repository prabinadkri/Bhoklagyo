package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.entity.Order;
import com.example.Bhoklagyo.entity.Restaurant;
import com.example.Bhoklagyo.entity.RestaurantMenuItem;
import com.example.Bhoklagyo.repository.OrderRepository;
import com.example.Bhoklagyo.repository.RestaurantMenuItemRepository;
import com.example.Bhoklagyo.repository.RestaurantRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class RestaurantAverageUpdater {

    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;
    private final RestaurantMenuItemRepository restaurantMenuItemRepository;
    public RestaurantAverageUpdater(RestaurantRepository restaurantRepository, OrderRepository orderRepository, RestaurantMenuItemRepository restaurantMenuItemRepository) {
        this.restaurantRepository = restaurantRepository;
        this.orderRepository = orderRepository;
        this.restaurantMenuItemRepository = restaurantMenuItemRepository;
    }

    @Scheduled(cron = "0 0 2 * * ?") // Run daily at 2 AM
    @Transactional
    public void updateAverageForOne() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        for (Restaurant restaurant : restaurants) {
            int average = calculateAverageForOne(restaurant);
            restaurant.setAverageForOne(average);
            restaurantRepository.save(restaurant);
        }
    }

    private int calculateAverageForOne(Restaurant restaurant) {
        long totalOrders = orderRepository.countByRestaurant(restaurant);
        List<Double> prices = new ArrayList<>();

        if (totalOrders < 100) {
            List<RestaurantMenuItem> menuItems = restaurantMenuItemRepository.findByRestaurantIdAndCategoryId(restaurant.getId(), 2L);
            for (RestaurantMenuItem menuItem : menuItems) {
                prices.add(menuItem.getPrice());
            }
            if (prices.isEmpty()) return 0;
            double sum = prices.stream().mapToDouble(Double::doubleValue).sum();
            return (int) Math.round(sum / prices.size());
            
        } else {
            // Check last 30 days
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            List<Order> recentOrders = orderRepository.findRecentOrdersByRestaurant(restaurant, thirtyDaysAgo);
            if (recentOrders.size() >= 100) {
                // Use last 30 days, median
                for (Order order : recentOrders) {
                    order.getOrderItems().forEach(item -> {
                        RestaurantMenuItem menuItem = item.getMenuItem();
                        if (menuItem.getCategory() != null && "Main Course".equals(menuItem.getCategory().getName())) {
                            prices.add(menuItem.getPrice());
                        }
                    });
                }
            } else {
                // Use last 100 orders, median
                List<Order> last100Orders = orderRepository.findOrdersByRestaurantOrderByCreatedAtDesc(restaurant, PageRequest.of(0, 100));
                for (Order order : last100Orders) {
                    order.getOrderItems().forEach(item -> {
                        RestaurantMenuItem menuItem = item.getMenuItem();
                        if (menuItem.getCategory() != null && "Main Course".equals(menuItem.getCategory().getName())) {
                            prices.add(menuItem.getPrice());
                        }
                    });
                }
            }
            if (prices.isEmpty()) return 0;
            Collections.sort(prices);
            int n = prices.size();
            if (n % 2 == 0) {
                return (int) Math.round((prices.get(n / 2 - 1) + prices.get(n / 2)) / 2);
            } else {
                return (int) Math.round(prices.get(n / 2));
            }
        }
    }
}