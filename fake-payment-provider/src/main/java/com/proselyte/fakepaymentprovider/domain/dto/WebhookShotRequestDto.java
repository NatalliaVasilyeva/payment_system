package com.proselyte.fakepaymentprovider.domain.dto;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


public record WebhookShotRequestDto(
    UUID providerTransactionId,
    String paymentMethod,
    BigDecimal amount,
    String currency,
    String type,
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