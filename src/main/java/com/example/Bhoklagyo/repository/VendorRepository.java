package com.example.Bhoklagyo.repository;

import com.example.Bhoklagyo.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    
    Optional<Vendor> findByPanNumber(String panNumber);
    
    boolean existsByPanNumber(String panNumber);
    
    Optional<Vendor> findByEmail(String email);
    
    boolean existsByEmail(String email);
}
