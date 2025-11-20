package com.example.Bhoklagyo.repository;

import com.example.Bhoklagyo.entity.RestaurantMenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantMenuItemRepository extends JpaRepository<RestaurantMenuItem, Long> {
    List<RestaurantMenuItem> findByRestaurantId(Long restaurantId);
    Optional<RestaurantMenuItem> findByRestaurantIdAndMenuItemId(Long restaurantId, Long menuItemId);
    List<RestaurantMenuItem> findByMenuItemId(Long menuItemId);
    boolean existsByRestaurantIdAndMenuItemId(Long restaurantId, Long menuItemId);
}
