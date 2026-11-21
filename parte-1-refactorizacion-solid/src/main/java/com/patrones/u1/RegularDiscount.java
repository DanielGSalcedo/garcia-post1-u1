package com.patrones.u1;

public final class RegularDiscount implements DiscountStrategy {
    @Override
    public double apply(double total) {
        return total * 0.95;
    }
}
