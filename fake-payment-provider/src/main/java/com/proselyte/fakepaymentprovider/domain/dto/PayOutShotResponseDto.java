package com.proselyte.fakepaymentprovider.domain.dto;

import com.proselyte.fakepaymentprovider.domain.model.PaymentStatus;

import java.util.UUID;

public record PayOutShotResponseDto(

    UUID payout_id,

    PaymentStatus status,

    String message) {
}