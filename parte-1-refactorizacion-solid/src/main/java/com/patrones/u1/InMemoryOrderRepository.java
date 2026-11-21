package com.patrones.u1;

import java.util.ArrayList;
import java.util.List;

public final class InMemoryOrderRepository implements OrderRepository {
    private final List<Order> orders = new ArrayList<>();

    @Override
    public void save(Order order) {
        orders.add(order);
    }

    public List<Order> findAll() {
        return List.copyOf(orders);
    }
}
