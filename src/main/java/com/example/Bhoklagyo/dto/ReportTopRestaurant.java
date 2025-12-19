package com.example.Bhoklagyo.dto;

public class ReportTopRestaurant {
    private String name;
    private Double revenue;
    private Long completedOrders;

    public ReportTopRestaurant() {}

    public ReportTopRestaurant(String name, Double revenue, Long completedOrders) {
        this.name = name;
        this.revenue = revenue;
        this.completedOrders = completedOrders;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getRevenue() {
        return revenue;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }

    public Long getCompletedOrders() {
        return completedOrders;
    }

    public void setCompletedOrders(Long completedOrders) {
        this.completedOrders = completedOrders;
    }
}
