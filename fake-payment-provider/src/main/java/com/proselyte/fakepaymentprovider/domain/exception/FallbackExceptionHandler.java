package com.proselyte.fakepaymentprovider.domain.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.lang.invoke.MethodHandles;

/**
 * Handler for exceptions raised in controllers flow.
 */
@ControllerAdvice
public class FallbackExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @ExceptionHandler({DomainResponseException.class})
    public ResponseEntity<ErrorResponse> handleCustomBadRequestExceptions(DomainResponseException ex) {
        LOG.info("Received invalid request payload, throwing exception {}: {}.", ex.getClass().getName(), ex.getMessage(), ex);
        return buildErrorResponseAsEntity(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


    private ResponseEntity<ErrorResponse> buildErrorResponseAsEntity(String message, HttpStatus status) {
        var errorResponse = ErrorResponse.builder().status(status).message(message).build();
        return new ResponseEntity<>(errorResponse, status);
    }
}