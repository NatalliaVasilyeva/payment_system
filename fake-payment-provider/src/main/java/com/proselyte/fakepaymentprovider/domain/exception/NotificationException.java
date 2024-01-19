package com.proselyte.fakepaymentprovider.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NotificationException extends ResponseStatusException {
    public NotificationException(HttpStatus httpStatus, String message) {

        super(httpStatus, message);
    }
}