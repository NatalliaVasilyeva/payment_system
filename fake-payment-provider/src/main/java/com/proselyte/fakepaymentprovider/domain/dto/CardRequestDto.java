package com.proselyte.fakepaymentprovider.domain.dto;

public record CardRequestDto(
    String cardNumber,
    String expDate,
    String cvv
) {

    public CardRequestDto(String cardNumber) {
        this(cardNumber, null, null);
    }
}