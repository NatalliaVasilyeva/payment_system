package com.proselyte.fakepaymentprovider.infrastructure.util;

import com.proselyte.fakepaymentprovider.domain.exception.TransactionBadRequestQueryException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Validator {

    private Validator() {
    }

    public static void validateCardExpirationDate(String expirationDate) throws ParseException {

        if (expirationDate.matches("(?:0\\d|1[0-2])/\\d{2}")) {
            var simpledateformat = new SimpleDateFormat("MM/yy");
            simpledateformat.setLenient(false);
            var formatExpirationDate = simpledateformat.parse(expirationDate);
            if (formatExpirationDate.before(new Date())) {
                throw new TransactionBadRequestQueryException("Card was expired");
            }
        } else {
            throw new TransactionBadRequestQueryException("Card expired date has wrong format");
        }
    }
}