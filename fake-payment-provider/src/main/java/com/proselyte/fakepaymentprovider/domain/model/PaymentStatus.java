package com.proselyte.fakepaymentprovider.domain.model;

import java.util.List;

public enum PaymentStatus {
    IN_PROGRESS,
    COMPLETED,
    APPROVED,
    FAILED;

    public static final List<PaymentStatus> ALLOWED_TRANSACTION_UPDATE_STATUS = List.of(APPROVED, FAILED);
    public static final List<PaymentStatus> ALLOWED_PAYOUT_UPDATE_STATUS = List.of(COMPLETED, FAILED);

    public static boolean isValidTransactionUpdateState(PaymentStatus paymentStatus) {
        return ALLOWED_TRANSACTION_UPDATE_STATUS.contains(paymentStatus);
    }

    public static boolean isValidPayoutUpdateState(PaymentStatus paymentStatus) {
        return ALLOWED_PAYOUT_UPDATE_STATUS.contains(paymentStatus);
    }
}