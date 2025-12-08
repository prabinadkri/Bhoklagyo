package com.example.Bhoklagyo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.Bhoklagyo.entity.Order;
import com.example.Bhoklagyo.entity.Restaurant;
import java.util.List;
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByRestaurant(Restaurant restaurant);
    List<Order> findByCustomerId(Long customerId);
    @Query("SELECT COALESCE(SUM(o.totalPrice), 0.0) FROM Order o")
    Double sumTotalRevenue();
}
