package com.example.Bhoklagyo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.Bhoklagyo.entity.Restaurant;
@Repository

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    
}
