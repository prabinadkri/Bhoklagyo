package com.example.Bhoklagyo.controller;

import com.example.Bhoklagyo.dto.DashboardReportResponse;
import com.example.Bhoklagyo.entity.Restaurant;
import com.example.Bhoklagyo.entity.User;
import com.example.Bhoklagyo.exception.ResourceNotFoundException;
import com.example.Bhoklagyo.repository.RestaurantRepository;
import com.example.Bhoklagyo.repository.UserRepository;
import com.example.Bhoklagyo.service.ReportService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class ReportController {

    private final ReportService reportService;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public ReportController(ReportService reportService, RestaurantRepository restaurantRepository, UserRepository userRepository) {
        this.reportService = reportService;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    @GetMapping("/restaurants/{restaurantId}/reports/dashboard")
    public ResponseEntity<DashboardReportResponse> getDashboardReport(
            @PathVariable Long restaurantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "daily") String interval
    ) {
        // Authorization: only restaurant owner or admin can request this report
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));

        boolean isOwner = restaurant.getOwner() != null && restaurant.getOwner().getId().equals(currentUser.getId());

        if (!isOwner) {
            throw new AccessDeniedException("You are not authorized to view this report");
        }

        DashboardReportResponse resp = reportService.getDashboardReport(restaurantId, startDate, endDate, interval);
        return ResponseEntity.ok(resp);
    }
}
