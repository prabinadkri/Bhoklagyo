package com.example.Bhoklagyo.repository;

import com.example.Bhoklagyo.entity.RestaurantOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantOwnerRepository extends JpaRepository<RestaurantOwner, Long> {
    Optional<RestaurantOwner> findByUsername(String username);
    boolean existsByUsername(String username);
}
