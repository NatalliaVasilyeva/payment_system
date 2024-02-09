package com.proselyte.fakepaymentprovider.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class TransactionBadRequestQueryException extends ResponseStatusException {
    public TransactionBadRequestQueryException(String message) {

        super(HttpStatus.BAD_REQUEST, message);
    }
}