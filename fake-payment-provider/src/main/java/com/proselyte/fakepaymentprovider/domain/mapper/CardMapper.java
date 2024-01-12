package com.proselyte.fakepaymentprovider.domain.mapper;

import com.proselyte.fakepaymentprovider.domain.dto.CardResponseDto;
import com.proselyte.fakepaymentprovider.domain.model.Transaction;

public class CardMapper {

    private CardMapper() {
    }

    public static CardResponseDto toCardResponseDto(Transaction transaction) {
        return new CardResponseDto(
            transaction.getCardNumber()
        );
    }

    public static CardResponseDto toCardResponseHideDto(Transaction transaction) {
        return new CardResponseDto(
            transaction.getCardNumber().replaceAll("(?<=.{3}).(?=.{3})", "*")
        );
    }
}