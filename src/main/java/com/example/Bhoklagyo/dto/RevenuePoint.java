package com.example.Bhoklagyo.dto;

public class RevenuePoint {
    private String period;
    private Double revenue;

    public RevenuePoint() {}

    public RevenuePoint(String period, Double revenue) {
        this.period = period;
        this.revenue = revenue;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Double getRevenue() {
        return revenue;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }
}
