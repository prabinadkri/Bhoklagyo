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
    List<RestaurantMenuItem> findByRestaurantId(Long restaurantId);
    Optional<RestaurantMenuItem> findByRestaurantIdAndCategoryId(Long restaurantId, Long categoryId);
    List<RestaurantMenuItem> findByCategoryId(Long categoryId);
    boolean existsByRestaurantIdAndCategoryId(Long restaurantId, Long categoryId);
    
    @Query("SELECT rmi FROM RestaurantMenuItem rmi WHERE LOWER(rmi.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(rmi.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<RestaurantMenuItem> searchByNameOrDescription(@Param("keyword") String keyword);
}
