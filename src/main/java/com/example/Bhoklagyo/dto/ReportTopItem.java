package com.example.Bhoklagyo.dto;

public class ReportTopItem {
    private String name;
    private Long quantity;

    public ReportTopItem() {}

    public ReportTopItem(String name, Long quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
}
