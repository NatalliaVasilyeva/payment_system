package com.proselyte.fakepaymentprovider.domain.mapper;

import com.proselyte.fakepaymentprovider.domain.dto.WebhookRequestDto;
import com.proselyte.fakepaymentprovider.domain.dto.WebhookShotRequestDto;
import com.proselyte.fakepaymentprovider.domain.entity.Transaction;
import com.proselyte.fakepaymentprovider.domain.entity.Webhook;

public class WebhookMapper {

    private WebhookMapper() {
    }


    public static WebhookShotRequestDto toWebhookShotRequestDto(WebhookRequestDto webhookRequestDto) {
        return new WebhookShotRequestDto(
            webhookRequestDto.providerTransactionId(),
            webhookRequestDto.paymentMethod(),
            webhookRequestDto.amount(),
            webhookRequestDto.currency(),
            webhookRequestDto.type(),
            webhookRequestDto.merchantTransactionId(),
            webhookRequestDto.createdAt(),
            webhookRequestDto.updatedAt(),
            webhookRequestDto.cardData(),
            webhookRequestDto.language(),
            webhookRequestDto.customer(),
            webhookRequestDto.status(),
            webhookRequestDto.message()
        );
    }

    public static Webhook toWebhook(WebhookRequestDto webhookRequestDto, int attempt, String result) {
        return Webhook.builder()
            .merchantId(webhookRequestDto.merchantId())
            .merchantTransactionId(webhookRequestDto.merchantTransactionId())
            .providerTransactionId(webhookRequestDto.providerTransactionId())
            .notificationUrl(webhookRequestDto.notificationUrl())
            .paymentMethod(webhookRequestDto.paymentMethod())
            .amount(webhookRequestDto.amount())
            .currency(webhookRequestDto.currency())
            .createdAt(webhookRequestDto.createdAt())
            .updatedAt(webhookRequestDto.updatedAt())
            .type(webhookRequestDto.type())
            .cardNumber(webhookRequestDto.cardData().cardNumber())
            .language(webhookRequestDto.language())
            .customerFirstName(webhookRequestDto.customer().firstName())
            .customerLastName(webhookRequestDto.customer().secondName())
            .status(webhookRequestDto.status())
            .message(webhookRequestDto.message())
            .attempt(attempt)
            .notificationResult(result)
            .build();
    }

    public static WebhookRequestDto toWebhookRequestDto(Transaction transaction) {
        return new WebhookRequestDto(
            transaction.getMerchantId(),
            transaction.getId(),
            transaction.getPaymentMethod(),
            transaction.getAmount(),
            transaction.getCurrency(),
            transaction.getType(),
            transaction.getNotificationUrl(),
            transaction.getMerchantTransactionId(),
            transaction.getCreatedAt(),
            transaction.getUpdatedAt(),
            CardMapper.toCardResponseDto(transaction),
            transaction.getLanguage(),
            CustomerMapper.toCustomerShotResponseDto(transaction),
            transaction.getStatus(),
            transaction.getMessage()
        );
    }
}