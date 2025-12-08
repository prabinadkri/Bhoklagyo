package com.example.Bhoklagyo.dto;

public class AdminDashboardResponse {
    long totalUsers;
    long totalRestaurants;
    long totalOrders;
    double totalRevenue;
    public AdminDashboardResponse(long totalUsers, long totalRestaurants, long totalOrders, double totalRevenue) {
        this.totalUsers = totalUsers;
        this.totalRestaurants = totalRestaurants;
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public long getTotalRestaurants() {
        return totalRestaurants;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }
    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }
    public void setTotalRestaurants(long totalRestaurants) {
        this.totalRestaurants = totalRestaurants;
    }
    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }
    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}