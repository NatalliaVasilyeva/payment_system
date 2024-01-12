package com.proselyte.fakepaymentprovider.domain.model;

public enum PaymentMessage {
    OK,
    PAYMENT_METHOD_NOT_ALLOWED,
    PAYOUT_IS_SUCCESSFULLY_COMPLETED,
    PAYOUT_MIN_AMOUNT,
    TRANSACTION_MIN_AMOUNT,
    TRANSACTION_IS_UNSUCCESSFULLY_NO_WALLET_FOR_CURRENCY_FOUND
    ;

}