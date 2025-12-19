package com.example.Bhoklagyo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @ManyToOne
    @JoinColumn(name = "restaurant_menu_item_id", nullable = false)
    private RestaurantMenuItem menuItem;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false)
    private Double priceAtOrder; // Store price at time of order
    
    public OrderItem() {}
    
    public OrderItem(Order order, RestaurantMenuItem menuItem, Integer quantity, Double priceAtOrder) {
        this.order = order;
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.priceAtOrder = priceAtOrder;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Order getOrder() {
        return order;
    }
    
    public void setOrder(Order order) {
        this.order = order;
    }
    
    public RestaurantMenuItem getMenuItem() {
        return menuItem;
    }
    
    public void setMenuItem(RestaurantMenuItem menuItem) {
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
        return priceAtOrder * quantity;
    }
}
