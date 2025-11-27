package com.example.Bhoklagyo.controller;

import com.example.Bhoklagyo.dto.RestaurantRequestDto;
import com.example.Bhoklagyo.dto.RestaurantRequestResponse;
import com.example.Bhoklagyo.dto.UpdateRequestStatusDto;
import com.example.Bhoklagyo.entity.RequestStatus;
import com.example.Bhoklagyo.service.RestaurantRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurant-requests")
public class RestaurantRequestController {

    @Autowired
    private RestaurantRequestService restaurantRequestService;

    @PostMapping
    public ResponseEntity<RestaurantRequestResponse> createRequest(@Valid @RequestBody RestaurantRequestDto requestDto) {
        RestaurantRequestResponse response = restaurantRequestService.createRequest(requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RestaurantRequestResponse>> getAllRequests(
            @RequestParam(required = false) RequestStatus status) {
        List<RestaurantRequestResponse> requests;
        if (status != null) {
            requests = restaurantRequestService.getRequestsByStatus(status);
        } else {
            requests = restaurantRequestService.getAllRequests();
        }
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestaurantRequestResponse> getRequestById(@PathVariable Long id) {
        RestaurantRequestResponse response = restaurantRequestService.getRequestById(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestaurantRequestResponse> updateRequestStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRequestStatusDto statusDto) {
        RestaurantRequestResponse response = restaurantRequestService.updateRequestStatus(id, statusDto.getStatus());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long id) {
        restaurantRequestService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }
}
