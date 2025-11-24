package com.example.Bhoklagyo.repository;

import com.example.Bhoklagyo.entity.DietaryTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DietaryTagRepository extends JpaRepository<DietaryTag, Long> {
    Optional<DietaryTag> findByName(String name);
}
