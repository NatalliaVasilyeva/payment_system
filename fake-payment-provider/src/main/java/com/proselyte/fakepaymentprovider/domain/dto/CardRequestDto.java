package com.proselyte.fakepaymentprovider.domain.dto;

import jakarta.validation.constraints.Size;

public record CardRequestDto(
    String cardNumber,
    String expDate,

    @Size(min = 3, max = 3)
    String cvv
) {

    public CardRequestDto(String cardNumber) {
        this(cardNumber, null, null);
    }
}