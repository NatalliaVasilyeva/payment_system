package com.proselyte.fakepaymentprovider.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


public record TopUpRequestDto(

    String paymentMethod,

    BigDecimal amount,

    String currency,

    UUID companyTransactionId,

    LocalDateTime createdAt,

    LocalDateTime updatedAt,

    CardRequestDto cardData,

    String language,

    String notificationUrl,

    CustomerRequestDto customer
    ) {

    public LocalDateTime createdAt() {
        return Optional.ofNullable(createdAt).orElse(LocalDateTime.now());
    }

    public LocalDateTime updatedAt() {
        return Optional.ofNullable(updatedAt).orElse(LocalDateTime.now());
    }


}