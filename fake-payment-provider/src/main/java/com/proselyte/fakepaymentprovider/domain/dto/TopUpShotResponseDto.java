package com.proselyte.fakepaymentprovider.domain.dto;

import com.proselyte.fakepaymentprovider.domain.model.PaymentMessage;
import com.proselyte.fakepaymentprovider.domain.model.PaymentStatus;

import java.util.UUID;

public record TopUpShotResponseDto(

    UUID transactionId,

    PaymentStatus status,

    PaymentMessage message) {
}