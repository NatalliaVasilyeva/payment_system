package com.proselyte.fakepaymentprovider.domain.model;

import java.util.List;

public enum PaymentMethod {

    CARD,
    CASH;


    public static final List<String> ALLOWED_PAYMENT_STATUS = List.of("CARD", "CASH");

    public static boolean isValidPaymentMethod(String paymentMethod) {
        return ALLOWED_PAYMENT_STATUS.contains(paymentMethod);
    }
}