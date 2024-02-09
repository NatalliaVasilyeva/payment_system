package com.proselyte.fakepaymentprovider.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PayoutBadRequestQueryException extends ResponseStatusException {
    public PayoutBadRequestQueryException(String message) {

        super(HttpStatus.BAD_REQUEST, message);
    }
}