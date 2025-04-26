package org.bobbyangyl.oms.controller;

import org.bobbyangyl.oms.model.Order;
import org.bobbyangyl.oms.service.MatchingEngine;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class TestOrderController {

    private final MatchingEngine matchingEngine;

    public TestOrderController(MatchingEngine matchingEngine) {
        this.matchingEngine = matchingEngine;
    }

    @PostMapping("/submit")
    public String submit(@RequestParam String id,
                         @RequestParam String symbol,
                         @RequestParam Order.Side side,
                         @RequestParam double price,
                         @RequestParam int quantity) {
        matchingEngine.submitOrder(id, symbol, side, price, quantity, System.currentTimeMillis());
        return "Order accepted";
    }

    @PostMapping("/spam")
    public String spamOrders(@RequestParam(defaultValue = "1000") int count) {
        for (int i = 0; i < count; i++) {
            matchingEngine.submitOrder(
                    "id-" + i,
                    "NVDA",
                    (i % 2 == 0) ? Order.Side.BUY : Order.Side.SELL,
                    100.0 + (i % 50),
                    10 + (i % 5),
                    System.currentTimeMillis()
            );
        }
        return count + " orders submitted.";
    }
}
