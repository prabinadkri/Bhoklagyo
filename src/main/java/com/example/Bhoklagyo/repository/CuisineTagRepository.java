package com.example.Bhoklagyo.repository;

import com.example.Bhoklagyo.entity.CuisineTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CuisineTagRepository extends JpaRepository<CuisineTag, Long> {
    Optional<CuisineTag> findByName(String name);
}
