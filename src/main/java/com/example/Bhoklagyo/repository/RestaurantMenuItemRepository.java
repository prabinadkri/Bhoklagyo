package com.example.Bhoklagyo.repository;

import com.example.Bhoklagyo.entity.RestaurantMenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantMenuItemRepository extends JpaRepository<RestaurantMenuItem, Long> {
    @Query("SELECT rmi FROM RestaurantMenuItem rmi WHERE rmi.restaurant.id = :restaurantId ORDER BY rmi.id ASC")
    List<RestaurantMenuItem> findByRestaurantId(@Param("restaurantId") Long restaurantId);
    
    Optional<RestaurantMenuItem> findByRestaurantIdAndCategoryId(Long restaurantId, Long categoryId);
    
    @Query("SELECT rmi FROM RestaurantMenuItem rmi WHERE rmi.category.id = :categoryId ORDER BY rmi.id ASC")
    List<RestaurantMenuItem> findByCategoryId(@Param("categoryId") Long categoryId);
    
    boolean existsByRestaurantIdAndCategoryId(Long restaurantId, Long categoryId);
    
    @Query("SELECT rmi FROM RestaurantMenuItem rmi WHERE LOWER(rmi.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(rmi.description) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY rmi.id ASC")
    List<RestaurantMenuItem> searchByNameOrDescription(@Param("keyword") String keyword);
}
