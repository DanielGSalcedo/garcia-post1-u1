package com.patrones.u1;

public final class EmailNotifier implements OrderNotifier {
    @Override
    public void sendConfirmation(String email, String orderId) {
        System.out.printf("[EMAIL SIMULADO] Confirmación de %s para %s%n", orderId, email);
    }
}
