package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.DashboardReportResponse;
import java.time.LocalDate;

public interface ReportService {
    DashboardReportResponse getDashboardReport(Long restaurantId, LocalDate startDate, LocalDate endDate, String interval);
}
