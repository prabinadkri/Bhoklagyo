package com.example.Bhoklagyo.repository;

import com.example.Bhoklagyo.entity.Document;
import com.example.Bhoklagyo.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    
    @Query("SELECT d FROM Document d JOIN d.restaurants r WHERE r.id = :restaurantId")
    List<Document> findByRestaurantId(@Param("restaurantId") Long restaurantId);
    
    List<Document> findByDocType(String docType);
    
    @Query("SELECT d FROM Document d WHERE d.expiryDate IS NOT NULL AND d.expiryDate < :date")
    List<Document> findExpiredDocuments(@Param("date") LocalDate date);
    
    @Query("SELECT d FROM Document d WHERE d.expiryDate IS NOT NULL AND d.expiryDate BETWEEN :startDate AND :endDate")
    List<Document> findDocumentsExpiringBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    boolean existsByDocumentNumber(String documentNumber);
}
