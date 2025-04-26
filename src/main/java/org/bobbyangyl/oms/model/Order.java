package org.bobbyangyl.oms.model;

import lombok.*;

@Builder
@Getter
@ToString
public class Order {

    public enum Side {
        BUY, SELL
    }

    private final String id;
    private final String symbol;
    private final Side side;
    private final double price;
    private final int quantity;
    private final long timestamp;

    public Order(String id, String symbol, Side side, double price, int quantity, long timestamp) {
        this.id = id;
        this.symbol = symbol;
        this.side = side;
        this.price = price;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }

    public Order withUpdatedQuantity(int newQuantity) {
        return new Order(this.id, this.symbol, this.side, this.price, newQuantity, this.timestamp);
    }

    public Order withUpdatedOrder(String id, String symbol, Side side, double price, int quantity, long timestamp) {
        return new Order(id, symbol, side, price, quantity, timestamp);
    }
}

