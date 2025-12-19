package com.example.Bhoklagyo.dto;

import java.util.List;

public class DashboardReportResponse {
    private Double totalRevenue;
    private Long totalOrders;
    private List<ReportTopItem> topItems;
    private List<RevenuePoint> revenueTrend;

    public DashboardReportResponse() {}

    public DashboardReportResponse(Double totalRevenue, Long totalOrders, List<ReportTopItem> topItems, List<RevenuePoint> revenueTrend) {
        this.totalRevenue = totalRevenue;
        this.totalOrders = totalOrders;
        this.topItems = topItems;
        this.revenueTrend = revenueTrend;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public List<ReportTopItem> getTopItems() {
        return topItems;
    }

    public void setTopItems(List<ReportTopItem> topItems) {
        this.topItems = topItems;
    }

    public List<RevenuePoint> getRevenueTrend() {
        return revenueTrend;
    }

    public void setRevenueTrend(List<RevenuePoint> revenueTrend) {
        this.revenueTrend = revenueTrend;
    }
}
