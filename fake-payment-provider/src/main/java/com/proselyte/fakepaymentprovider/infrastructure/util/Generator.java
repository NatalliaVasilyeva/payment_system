package com.proselyte.fakepaymentprovider.infrastructure.util;

import com.proselyte.fakepaymentprovider.domain.model.PaymentStatus;

import java.util.Random;

public class Generator {

    private static final Random random = new Random();

    private Generator() {
    }

    public static PaymentStatus generateRandomTransactionStatus() {
        return random.nextBoolean() ? PaymentStatus.APPROVED : PaymentStatus.FAILED;
    }

    public static PaymentStatus generateRandomPayoutStatus() {
        return random.nextBoolean() ? PaymentStatus.COMPLETED : PaymentStatus.FAILED;
    }
}