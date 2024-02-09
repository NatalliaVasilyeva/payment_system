package com.proselyte.fakepaymentprovider.domain.dto;

public record MerchantRequestDto(
    String clientId,
    String clientSecret
) {
}