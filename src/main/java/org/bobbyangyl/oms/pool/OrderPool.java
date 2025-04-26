package org.bobbyangyl.oms.pool;

import org.bobbyangyl.oms.model.Order;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class OrderPool {

    private final Queue<Order> pool = new ConcurrentLinkedQueue<>();

    public OrderPool() {
        // Pre-populate Obj pool with 100K Order Objects AOT
        for (int i = 0; i < 100_000; i++) {
            pool.offer(new Order());
        }
    }

    public Order acquire() {
        Order order = pool.poll();
        return (order != null) ? order : new Order(); // fallback
    }

    public void release(Order order) {
        pool.offer(order);
    }
}

