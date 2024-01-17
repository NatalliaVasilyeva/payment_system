package com.proselyte.fakepaymentprovider.infrastructure.util;

import com.proselyte.fakepaymentprovider.domain.exception.TransactionBadRequestQueryException;

import java.time.LocalDate;

public class Validator {

    private Validator() {
    }

    public static void validateCardExpirationDate(String expirationDate) {
        var expDateSplitArray = expirationDate.split(":");
        var month = Integer.getInteger(expDateSplitArray[0]);
        var year = Integer.getInteger(expDateSplitArray[1]);
        LocalDate currentDate = LocalDate.now();

        if (month < currentDate.getMonthValue() && year < currentDate.getYear()) {
            throw new TransactionBadRequestQueryException("Card was expired");
        }

    }
}