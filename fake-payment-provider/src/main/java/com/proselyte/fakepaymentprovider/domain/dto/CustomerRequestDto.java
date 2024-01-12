package com.proselyte.fakepaymentprovider.domain.dto;

public record CustomerRequestDto(
    String firstName,
    String secondName,
    String country
) {
}