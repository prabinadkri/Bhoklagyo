package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.RestaurantRequestDto;
import com.example.Bhoklagyo.dto.RestaurantRequestResponse;
import com.example.Bhoklagyo.entity.RequestStatus;

import java.util.List;

public interface RestaurantRequestService {
    RestaurantRequestResponse createRequest(RestaurantRequestDto requestDto);
    List<RestaurantRequestResponse> getAllRequests();
    List<RestaurantRequestResponse> getRequestsByStatus(RequestStatus status);
    RestaurantRequestResponse getRequestById(Long id);
    RestaurantRequestResponse updateRequestStatus(Long id, RequestStatus status);
    void deleteRequest(Long id);
}
