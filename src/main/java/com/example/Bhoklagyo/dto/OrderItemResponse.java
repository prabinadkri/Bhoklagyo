package com.example.Bhoklagyo.dto;

public class OrderItemResponse {
    private Long id;
    private MenuItemResponse menuItem;
    private Integer quantity;
    private Double priceAtOrder;
    private Double subtotal;
    
    public OrderItemResponse() {}
    
    public OrderItemResponse(Long id, MenuItemResponse menuItem, Integer quantity, Double priceAtOrder, Double subtotal) {
        this.id = id;
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.priceAtOrder = priceAtOrder;
        this.subtotal = subtotal;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public MenuItemResponse getMenuItem() {
        return menuItem;
    }
    
    public void setMenuItem(MenuItemResponse menuItem) {
        this.menuItem = menuItem;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public Double getPriceAtOrder() {
        return priceAtOrder;
    }
    
    public void setPriceAtOrder(Double priceAtOrder) {
        this.priceAtOrder = priceAtOrder;
    }
    
    public Double getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }
}
