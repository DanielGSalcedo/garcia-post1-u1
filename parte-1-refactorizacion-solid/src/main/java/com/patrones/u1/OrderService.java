package com.patrones.u1;

import java.util.List;

public final class OrderService {
    private final TotalCalculator calculator;
    private final OrderRepository repository;
    private final OrderNotifier notifier;
    private final DiscountStrategy discount;

    public OrderService(TotalCalculator calculator, OrderRepository repository,
                        OrderNotifier notifier, DiscountStrategy discount) {
        this.calculator = calculator;
        this.repository = repository;
        this.notifier = notifier;
        this.discount = discount;
    }

    public void processOrder(String orderId, String email, List<Double> prices) {
        double total = discount.apply(calculator.calculateTotal(prices));
        repository.save(new Order(orderId, total));
        notifier.sendConfirmation(email, orderId);
    }
}
