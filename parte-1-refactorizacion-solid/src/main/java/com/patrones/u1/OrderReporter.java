package com.patrones.u1;

import java.util.List;
import java.util.Locale;

public final class OrderReporter {
    public void print(List<Order> orders) {
        System.out.println("=== Reporte de órdenes ===");
        for (Order order : orders) {
            System.out.printf(Locale.ROOT, "  %s: %.2f%n", order.id(), order.total());
        }
    }
}
