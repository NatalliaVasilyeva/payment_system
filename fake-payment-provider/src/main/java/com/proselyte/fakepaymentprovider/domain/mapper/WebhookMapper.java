package com.proselyte.fakepaymentprovider.domain.mapper;

import com.proselyte.fakepaymentprovider.domain.model.Transaction;
import com.proselyte.fakepaymentprovider.domain.model.Webhook;


public class WebhookMapper {

    private WebhookMapper() {
    }


    public static Webhook toWebhook(Transaction transaction) {
        return Webhook.builder()
            .merchantId(transaction.getMerchantId())
            .merchantTransactionId(transaction.getMerchantTransactionId())
            .notificationUrl(transaction.getNotificationUrl())
            .paymentMethod(transaction.getPaymentMethod())
            .amount(transaction.getAmount())
            .currency(transaction.getCurrency())
            .createdAt(transaction.getCreatedAt())
            .updatedAt(transaction.getUpdatedAt())
            .type(transaction.isTransactional() ? "transaction" : "payout")
            .cardNumber(transaction.getCardNumber())
            .language(transaction.getLanguage())
            .customerFirstName(transaction.getCustomerFirstName())
            .customerLastName(transaction.getCustomerLastName())
            .status(transaction.getStatus())
            .message(transaction.getMessage())
            .build();

    }
}