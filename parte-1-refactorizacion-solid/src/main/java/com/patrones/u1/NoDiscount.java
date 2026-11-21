package com.patrones.u1;

public final class NoDiscount implements DiscountStrategy {
    @Override
    public double apply(double total) {
        return total;
    }
}
