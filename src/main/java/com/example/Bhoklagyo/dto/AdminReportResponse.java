package com.example.Bhoklagyo.dto;

import java.util.List;

public class AdminReportResponse {
    private Double totalRevenue;
    private Long totalOrders;
    private List<ReportTopRestaurant> topRestaurants;
    private List<RevenuePoint> revenueTrend;

    public AdminReportResponse() {}

    public AdminReportResponse(Double totalRevenue, Long totalOrders, List<ReportTopRestaurant> topRestaurants, List<RevenuePoint> revenueTrend) {
        this.totalRevenue = totalRevenue;
        this.totalOrders = totalOrders;
        this.topRestaurants = topRestaurants;
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

    public List<ReportTopRestaurant> getTopRestaurants() {
        return topRestaurants;
    }

    public void setTopRestaurants(List<ReportTopRestaurant> topRestaurants) {
        this.topRestaurants = topRestaurants;
    }

    public List<RevenuePoint> getRevenueTrend() {
        return revenueTrend;
    }

    public void setRevenueTrend(List<RevenuePoint> revenueTrend) {
        this.revenueTrend = revenueTrend;
    }
}
