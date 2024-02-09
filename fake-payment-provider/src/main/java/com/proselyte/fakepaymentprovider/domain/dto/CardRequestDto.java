package com.proselyte.fakepaymentprovider.domain.dto;

import jakarta.validation.constraints.Size;
import reactor.util.annotation.Nullable;

public record CardRequestDto(
    String cardNumber,

    @Nullable
    String expDate,

    @Nullable
    @Size(min = 3, max = 3)
    Integer cvv
) {

    public CardRequestDto(String cardNumber) {
        this(cardNumber, null, null);
    }
}