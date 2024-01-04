package com.proselyte.fakepaymentprovider.domain.model;

import java.util.List;

public enum PaymentStatus {
    IN_PROGRESS,
    COMPLETED,
    FAILED;

    public static final List<PaymentStatus> ALLOWED_UPDATE_STATUS = List.of(COMPLETED, FAILED);

    public static boolean isValidUpdateState(PaymentStatus paymentStatus) {
        return ALLOWED_UPDATE_STATUS.contains(paymentStatus);
    }
}