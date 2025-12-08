package com.example.Bhoklagyo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.Bhoklagyo.entity.Restaurant;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    @Query("SELECT r FROM Restaurant r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Restaurant> searchByName(@Param("keyword") String keyword);
    
    @Query("SELECT DISTINCT r FROM Restaurant r JOIN r.cuisineTags ct WHERE LOWER(ct.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Restaurant> searchByCuisineTag(@Param("keyword") String keyword);
    
    List<Restaurant> findByIsFeaturedTrue();
    
    @Query("SELECT r FROM Restaurant r WHERE r.id > :cursor ORDER BY r.id ASC")
    List<Restaurant> findAllWithCursor(@Param("cursor") Long cursor, org.springframework.data.domain.Pageable pageable);
    
    @Query("SELECT r FROM Restaurant r ORDER BY r.id ASC")
    List<Restaurant> findAllOrdered(org.springframework.data.domain.Pageable pageable);
    
    @Query("SELECT r FROM Restaurant r WHERE (LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND r.id > :cursor ORDER BY r.id ASC")
    List<Restaurant> searchByNameWithCursor(@Param("keyword") String keyword, @Param("cursor") Long cursor, org.springframework.data.domain.Pageable pageable);
    
    @Query("SELECT r FROM Restaurant r WHERE (LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) ORDER BY r.id ASC")
    List<Restaurant> searchByNameOrdered(@Param("keyword") String keyword, org.springframework.data.domain.Pageable pageable);
    
    @Query("SELECT DISTINCT r FROM Restaurant r JOIN r.cuisineTags ct WHERE LOWER(ct.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND r.id > :cursor ORDER BY r.id ASC")
    List<Restaurant> searchByCuisineTagWithCursor(@Param("keyword") String keyword, @Param("cursor") Long cursor, org.springframework.data.domain.Pageable pageable);
    
    @Query("SELECT DISTINCT r FROM Restaurant r JOIN r.cuisineTags ct WHERE LOWER(ct.name) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY r.id ASC")
    List<Restaurant> searchByCuisineTagOrdered(@Param("keyword") String keyword, org.springframework.data.domain.Pageable pageable);
    
    List<Restaurant> findByOwnerId(Long ownerId);
}
