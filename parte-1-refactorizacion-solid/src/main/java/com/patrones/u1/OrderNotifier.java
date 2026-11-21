package com.patrones.u1;

public interface OrderNotifier {
    void sendConfirmation(String email, String orderId);
}
