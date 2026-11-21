package com.patrones.u1;

import java.util.List;

public final class TaxCalculator implements TotalCalculator {
    private final double taxRate;

    public TaxCalculator(double taxRate) {
        this.taxRate = taxRate;
    }

    @Override
    public double calculateTotal(List<Double> prices) {
        double subtotal = prices.stream().mapToDouble(Double::doubleValue).sum();
        return subtotal + subtotal * taxRate;
    }
}
