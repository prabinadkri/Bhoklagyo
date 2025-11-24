package com.example.Bhoklagyo.dto;

import com.example.Bhoklagyo.entity.OrderStatus;

public class OrderStatusRequest {
    private OrderStatus status;

    public OrderStatusRequest() {}

    public OrderStatusRequest(OrderStatus status) {
        this.status = status;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
