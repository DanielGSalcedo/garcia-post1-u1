package com.patrones.u1;

import java.util.List;

public final class Main {
    public static void main(String[] args) {
        var repository = new InMemoryOrderRepository();
        var calculator = new TaxCalculator(0.19);
        var notifier = new EmailNotifier();
        var reporter = new OrderReporter();

        var vip = new OrderService(calculator, repository, notifier, new VipDiscount());
        vip.processOrder("ORD-001", "vip@example.com", List.of(100.0, 200.0, 50.0));

        var regular = new OrderService(calculator, repository, notifier, new RegularDiscount());
        regular.processOrder("ORD-002", "regular@example.com", List.of(80.0, 120.0));

        var standard = new OrderService(calculator, repository, notifier, new NoDiscount());
        standard.processOrder("ORD-003", "standard@example.com", List.of(50.0));

        reporter.print(repository.findAll());
    }
}
