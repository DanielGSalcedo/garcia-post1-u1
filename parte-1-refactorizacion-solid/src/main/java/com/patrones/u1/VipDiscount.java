package com.patrones.u1;

public final class VipDiscount implements DiscountStrategy {
    @Override
    public double apply(double total) {
        return total * 0.85;
    }
}
