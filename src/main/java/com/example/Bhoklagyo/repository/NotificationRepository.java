package com.example.Bhoklagyo.repository;

import com.example.Bhoklagyo.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Notification> findByRestaurantIdOrderByCreatedAtDesc(Long restaurantId);

    @Modifying
    @Transactional
    @Query("update Notification n set n.isRead = true where n.userId = :userId and n.isRead = false")
    int markAllAsReadByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("update Notification n set n.isRead = true where n.restaurantId = :restaurantId and n.isRead = false and n.userId is null")
    int markAllAsReadByRestaurantId(@Param("restaurantId") Long restaurantId);

    @Modifying
    @Transactional
    @Query("delete from Notification n where n.userId = :userId")
    int deleteAllByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("delete from Notification n where n.restaurantId = :restaurantId and n.userId is null")
    int deleteAllByRestaurantId(@Param("restaurantId") Long restaurantId);
}
