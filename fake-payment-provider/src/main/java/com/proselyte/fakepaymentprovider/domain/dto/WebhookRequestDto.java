package com.proselyte.fakepaymentprovider.domain.dto;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record WebhookRequestDto(
    UUID merchantId,
    UUID providerTransactionId,
    String paymentMethod,
    BigDecimal amount,
    String currency,
    String type,
    String notificationUrl,
    UUID merchantTransactionId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    CardResponseDto cardData,
    String language,
    CustomerShortResponseDto customer,
    String status,
    String message
) {

}