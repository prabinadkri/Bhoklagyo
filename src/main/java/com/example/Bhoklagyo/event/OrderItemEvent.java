package com.example.Bhoklagyo.event;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Represents a single item within an OrderEvent.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderItemEvent {

    private Long menuItemId;
    private String menuItemName;
    private Integer quantity;
    private Double priceAtOrder;
    private Double subtotal;

    public OrderItemEvent() {}

    public OrderItemEvent(Long menuItemId, String menuItemName, Integer quantity,
                          Double priceAtOrder, Double subtotal) {
        this.menuItemId = menuItemId;
        this.menuItemName = menuItemName;
        this.quantity = quantity;
        this.priceAtOrder = priceAtOrder;
        this.subtotal = subtotal;
    }

    public Long getMenuItemId() { return menuItemId; }
    public void setMenuItemId(Long menuItemId) { this.menuItemId = menuItemId; }

    public String getMenuItemName() { return menuItemName; }
    public void setMenuItemName(String menuItemName) { this.menuItemName = menuItemName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getPriceAtOrder() { return priceAtOrder; }
    public void setPriceAtOrder(Double priceAtOrder) { this.priceAtOrder = priceAtOrder; }

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
}
