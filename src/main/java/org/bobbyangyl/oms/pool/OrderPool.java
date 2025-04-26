package org.bobbyangyl.oms.pool;

import org.bobbyangyl.oms.model.Order;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class OrderPool {

    private final Queue<Order> pool = new ConcurrentLinkedQueue<>();

    public Order acquire() {
        Order order = pool.poll();
        return (order != null) ? order : new Order();
    }

    public void release(Order order) {
        pool.offer(order);
    }
}
