package com.proselyte.fakepaymentprovider.domain.dto;

public record CustomerRequestDto(
    String firstName,
    String lastName,
    String country
) {
}