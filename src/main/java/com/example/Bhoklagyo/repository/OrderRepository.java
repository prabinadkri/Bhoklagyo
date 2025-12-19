package com.example.Bhoklagyo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.Bhoklagyo.entity.Order;
import com.example.Bhoklagyo.entity.Restaurant;
import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByRestaurant(Restaurant restaurant);
    List<Order> findByCustomerId(Long customerId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.restaurant = :restaurant")
    long countByRestaurant(@Param("restaurant") Restaurant restaurant);

    @Query("SELECT o FROM Order o WHERE o.restaurant = :restaurant AND o.orderTime >= :since ORDER BY o.orderTime DESC")
    List<Order> findRecentOrdersByRestaurant(@Param("restaurant") Restaurant restaurant, @Param("since") LocalDateTime since);

    @Query("SELECT o FROM Order o WHERE o.restaurant = :restaurant ORDER BY o.orderTime DESC")
    List<Order> findOrdersByRestaurantOrderByCreatedAtDesc(@Param("restaurant") Restaurant restaurant, org.springframework.data.domain.Pageable pageable);
}
