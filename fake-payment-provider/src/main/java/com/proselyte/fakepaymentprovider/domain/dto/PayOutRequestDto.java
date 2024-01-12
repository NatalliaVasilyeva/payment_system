package com.proselyte.fakepaymentprovider.domain.dto;


import java.math.BigDecimal;
import java.util.UUID;


//@ParameterObject
public record PayOutRequestDto(

    String paymentMethod,

    BigDecimal amount,

    String currency,

    UUID companyTransactionId,

    CardRequestDto cardData,

    String language,

    String notificationUrl,

    CustomerRequestDto customerRequestDto
    ) {
}