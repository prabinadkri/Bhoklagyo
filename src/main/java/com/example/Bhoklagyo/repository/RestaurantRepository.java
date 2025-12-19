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

    @Query(value = "SELECT r.* FROM restaurants r "
            + "LEFT JOIN restaurant_cuisine_tags rct ON r.id = rct.restaurant_id "
            + "LEFT JOIN cuisine_tags ct ON rct.cuisine_tag_id = ct.id "
            + "WHERE (LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "OR LOWER(ct.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
            + "AND ( (:cursorId IS NULL) OR (ST_Distance(r.location, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)) > (SELECT ST_Distance(r2.location, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)) FROM restaurants r2 WHERE r2.id = :cursorId)) "
            + "OR (ST_Distance(r.location, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)) = (SELECT ST_Distance(r2.location, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)) FROM restaurants r2 WHERE r2.id = :cursorId) AND r.id > :cursorId) ) "
            + "GROUP BY r.id "
            + "ORDER BY ST_Distance(r.location, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)) ASC, r.id ASC "
            + "LIMIT :limit", nativeQuery = true)
    List<Restaurant> searchByKeywordOrderByDistanceNativeWithCursor(@Param("keyword") String keyword,
                                                                    @Param("lat") double lat,
                                                                    @Param("lon") double lon,
                                                                    @Param("cursorId") Long cursorId,
                                                                    @Param("limit") int limit);



    @Query("SELECT r FROM Restaurant r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND ((r.rating < :cursorRating) OR (r.rating = :cursorRating AND r.id > :cursorId)) ORDER BY r.rating DESC, r.id ASC")
    List<Restaurant> searchByNameWithCursorRated(@Param("keyword") String keyword, @Param("cursorRating") Double cursorRating, @Param("cursorId") Long cursorId, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT r FROM Restaurant r JOIN r.cuisineTags ct WHERE LOWER(ct.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND ((r.rating < :cursorRating) OR (r.rating = :cursorRating AND r.id > :cursorId)) ORDER BY r.rating DESC, r.id ASC")
    List<Restaurant> searchByCuisineTagWithCursorRated(@Param("keyword") String keyword, @Param("cursorRating") Double cursorRating, @Param("cursorId") Long cursorId, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT r FROM Restaurant r WHERE (LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) ORDER BY r.rating DESC")
    List<Restaurant> searchByNameRated(@Param("keyword") String keyword, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT r FROM Restaurant r JOIN r.cuisineTags ct WHERE LOWER(ct.name) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY r.rating DESC")
    List<Restaurant> searchByCuisineTagRated(@Param("keyword") String keyword, org.springframework.data.domain.Pageable pageable);
    
    @Query("SELECT r FROM Restaurant r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY CASE WHEN r.averageForOne = 0 THEN 999999 ELSE r.averageForOne END ASC, r.id ASC")
    List<Restaurant> searchByNameOrderedByPrice(@Param("keyword") String keyword, org.springframework.data.domain.Pageable pageable);
    
    @Query("SELECT r FROM Restaurant r JOIN r.cuisineTags ct WHERE LOWER(ct.name) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY CASE WHEN r.averageForOne = 0 THEN 999999 ELSE r.averageForOne END ASC, r.id ASC")
    List<Restaurant> searchByCuisineTagOrderedByPrice(@Param("keyword") String keyword, org.springframework.data.domain.Pageable pageable);
    
    @Query("SELECT r FROM Restaurant r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND r.id > :cursor ORDER BY CASE WHEN r.averageForOne = 0 THEN 999999 ELSE r.averageForOne END ASC, r.id ASC")
    List<Restaurant> searchByNameWithCursorOrderedByPrice(@Param("keyword") String keyword, @Param("cursor") Long cursor, org.springframework.data.domain.Pageable pageable);
    
    @Query("SELECT r FROM Restaurant r JOIN r.cuisineTags ct WHERE LOWER(ct.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND r.id > :cursor ORDER BY CASE WHEN r.averageForOne = 0 THEN 999999 ELSE r.averageForOne END ASC, r.id ASC")
    List<Restaurant> searchByCuisineTagWithCursorOrderedByPrice(@Param("keyword") String keyword, @Param("cursor") Long cursor, org.springframework.data.domain.Pageable pageable);
    
}
