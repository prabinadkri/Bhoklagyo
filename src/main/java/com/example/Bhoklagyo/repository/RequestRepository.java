package com.example.Bhoklagyo.repository;

import com.example.Bhoklagyo.entity.Request;
import com.example.Bhoklagyo.entity.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    
    List<Request> findByStatus(RequestStatus status);
    
    List<Request> findByStatusOrderByRequestIdDesc(RequestStatus status);
    
    List<Request> findAllByOrderByRequestIdDesc();
}
