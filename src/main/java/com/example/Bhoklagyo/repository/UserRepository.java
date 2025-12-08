package com.example.Bhoklagyo.repository;

import com.example.Bhoklagyo.entity.Role;
import com.example.Bhoklagyo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
    List<User> findByEmployedRestaurantId(Long restaurantId);
    @Query("SELECT u FROM User u WHERE u.role = 'EMPLOYEE' AND u.employedRestaurant.id = ?1")
    List<User> findEmployeesByRestaurantId(Long restaurantId);
}
