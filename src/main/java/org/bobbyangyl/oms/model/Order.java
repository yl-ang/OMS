package org.bobbyangyl.oms.model;

import lombok.*;

@Builder
@Getter
@ToString
public class Order {

    public enum Side {
        BUY, SELL
    }

    private String id;
    private String symbol;
    private Side side;
    private double price;
    private int quantity;
    private long timestamp;

    public Order() {}

    public Order(String id, String symbol, Side side, double price, int quantity, long timestamp) {
        this.id = id;
        this.symbol = symbol;
        this.side = side;
        this.price = price;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }

    // Recycle Order Object
    public void reset(String id, String symbol, Side side, double price, int quantity, long timestamp) {
        this.id = id;
        this.symbol = symbol;
        this.side = side;
        this.price = price;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }

    // Recycle Order Object by overwriting qty
    public void resetQuantity(int newQuantity) {
        this.quantity = newQuantity;
    }
}