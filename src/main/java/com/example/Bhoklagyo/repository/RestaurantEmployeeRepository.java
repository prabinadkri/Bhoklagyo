package com.example.Bhoklagyo.repository;

import com.example.Bhoklagyo.entity.RestaurantEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantEmployeeRepository extends JpaRepository<RestaurantEmployee, Long> {
    Optional<RestaurantEmployee> findByUsername(String username);
    List<RestaurantEmployee> findByRestaurantId(Long restaurantId);
}
