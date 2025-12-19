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
    
    List<RestaurantMenuItem> findByRestaurantIdAndCategoryId(Long restaurantId, Long categoryId);
    
    @Query("SELECT rmi FROM RestaurantMenuItem rmi WHERE rmi.category.id = :categoryId ORDER BY rmi.id ASC")
    List<RestaurantMenuItem> findByCategoryId(@Param("categoryId") Long categoryId);
    
    boolean existsByRestaurantIdAndCategoryId(Long restaurantId, Long categoryId);
    
    @Query("SELECT rmi FROM RestaurantMenuItem rmi WHERE LOWER(rmi.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(rmi.description) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY rmi.id ASC")
    List<RestaurantMenuItem> searchByNameOrDescription(@Param("keyword") String keyword);

    void deleteByRestaurantId(Long restaurantId);

        @Query(value = "SELECT rmi.* FROM restaurant_menu_items rmi "
            + "JOIN restaurants r ON rmi.restaurant_id = r.id "
            + "WHERE (LOWER(rmi.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(rmi.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
            + "ORDER BY ST_Distance(r.location, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)) ASC "
            + "LIMIT :limit", nativeQuery = true)
        List<RestaurantMenuItem> searchByNameOrDescriptionOrderByRestaurantDistance(@Param("keyword") String keyword,
                                            @Param("lat") double lat,
                                            @Param("lon") double lon,
                                            @Param("limit") Integer limit);
}
