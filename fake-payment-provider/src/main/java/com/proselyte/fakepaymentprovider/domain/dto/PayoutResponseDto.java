package com.proselyte.fakepaymentprovider.domain.dto;

import com.proselyte.fakepaymentprovider.domain.model.PaymentMessage;
import com.proselyte.fakepaymentprovider.domain.model.PaymentMethod;
import com.proselyte.fakepaymentprovider.domain.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PayoutResponseDto(

    UUID payoutId,
    PaymentMethod paymentMethod,
    BigDecimal amount,
    String currency,
    UUID companyTransactionId,

    LocalDateTime createdAt,

    LocalDateTime updatedAt,

    String notificationUrl,

    CardResponseDto cardData,

    String language,

    CustomerResponseDto customer,

    PaymentStatus status,

    PaymentMessage message

   ) {
}