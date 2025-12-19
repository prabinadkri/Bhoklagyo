package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.AdminReportResponse;

import java.time.LocalDate;

public interface AdminReportService {
    AdminReportResponse getAdminReport(LocalDate startDate, LocalDate endDate, String interval, int limit);
}
