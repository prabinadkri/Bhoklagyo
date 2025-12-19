package com.example.Bhoklagyo.controller;

import com.example.Bhoklagyo.dto.AdminReportResponse;
import com.example.Bhoklagyo.service.AdminReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class AdminReportController {

    private final AdminReportService adminReportService;

    public AdminReportController(AdminReportService adminReportService) {
        this.adminReportService = adminReportService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/reports/dashboard")
    public ResponseEntity<AdminReportResponse> getAdminDashboard(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "daily") String interval,
            @RequestParam(defaultValue = "5") int limit
    ) {
        AdminReportResponse resp = adminReportService.getAdminReport(startDate, endDate, interval, limit);
        return ResponseEntity.ok(resp);
    }
}
