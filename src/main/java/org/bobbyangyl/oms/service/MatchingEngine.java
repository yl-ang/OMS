package org.bobbyangyl.oms.service;

import lombok.extern.slf4j.Slf4j;
import org.bobbyangyl.oms.model.Order;
import org.bobbyangyl.oms.pool.OrderPool;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class MatchingEngine {

    private final BlockingQueue<Order> orderQueue = new LinkedBlockingQueue<>();
    private final ExecutorService matchingExecutor = Executors.newSingleThreadExecutor();
    private final OrderPool orderPool = new OrderPool();
    private final Map<String, TreeMap<Double, Queue<Order>>> buyOrders = new TreeMap<>();
    private final Map<String, TreeMap<Double, Queue<Order>>> sellOrders = new TreeMap<>();

    public MatchingEngine() {
        matchingExecutor.submit(this::processOrders);
    }

    public void submitOrder(String id, String symbol, Order.Side side, double price, int quantity, long timestamp) {
        Order order = orderPool.acquire();
        order.reset(id, symbol, side, price, quantity, timestamp);
        orderQueue.offer(order);
    }

    private void processOrders() {
        while (true) {
            try {
                Order order = orderQueue.take();
                match(order);
                orderPool.release(order);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void match(Order order) {
        Map<String, TreeMap<Double, Queue<Order>>> counterOrders =
                (order.getSide() == Order.Side.BUY) ? sellOrders : buyOrders;

        TreeMap<Double, Queue<Order>> pricePoints =
                counterOrders.computeIfAbsent(order.getSymbol(), k -> new TreeMap<>());

        while (order.getQuantity() > 0 && !pricePoints.isEmpty()) {
            Map.Entry<Double, Queue<Order>> bestPriceEntry =
                    (order.getSide() == Order.Side.BUY) ? pricePoints.firstEntry() : pricePoints.lastEntry();

            double bestPrice = bestPriceEntry.getKey();

            if ((order.getSide() == Order.Side.BUY && order.getPrice() < bestPrice) ||
                    (order.getSide() == Order.Side.SELL && order.getPrice() > bestPrice)) {
                break;
            }

            Queue<Order> ordersAtBestPrice = bestPriceEntry.getValue();
            Order matchingOrder = ordersAtBestPrice.peek();

            if (matchingOrder == null) {
                pricePoints.remove(bestPrice);
                continue;
            }

            int matchedQuantity = Math.min(order.getQuantity(), matchingOrder.getQuantity());

            // Reduce quantity in-place
            order.resetQuantity(order.getQuantity() - matchedQuantity);
            matchingOrder.resetQuantity(matchingOrder.getQuantity() - matchedQuantity);

            if (matchingOrder.getQuantity() == 0) {
                ordersAtBestPrice.poll();
                if (ordersAtBestPrice.isEmpty()) {
                    pricePoints.remove(bestPrice);
                }
                // Recycle matchingOrder
                orderPool.release(matchingOrder);
            }
        }

        // If partially unmatched, add back to own side
        if (order.getQuantity() > 0) {
            Map<String, TreeMap<Double, Queue<Order>>> sameTypeOrders =
                    (order.getSide() == Order.Side.BUY) ? buyOrders : sellOrders;

            TreeMap<Double, Queue<Order>> orderBook =
                    sameTypeOrders.computeIfAbsent(order.getSymbol(), k -> new TreeMap<>());

            orderBook.computeIfAbsent(order.getPrice(), k -> new LinkedList<>()).offer(order);
        }
    }
}