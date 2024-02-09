package com.proselyte.fakepaymentprovider.domain.dto;

public record CustomerResponseDto(
    String firstName,
    String secondName,
    String country
) {
}