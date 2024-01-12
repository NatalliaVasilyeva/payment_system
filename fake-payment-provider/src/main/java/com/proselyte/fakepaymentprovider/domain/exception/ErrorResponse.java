package com.proselyte.fakepaymentprovider.domain.exception;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.http.HttpStatus;


@EqualsAndHashCode
@ToString
public class ErrorResponse {

    private int statusCode;

    private String status;

    private String message;

    ErrorResponse(HttpStatus status, String message) {
        statusCode = status.value();
        this.status = status.getReasonPhrase();
        this.message = message;
    }

    public ErrorResponse() {
        // Jackson
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public static ExceptionResponseBuilder builder() {
        return new ExceptionResponseBuilder();
    }

    public static class ExceptionResponseBuilder {
        private HttpStatus status;

        private String message;

        ExceptionResponseBuilder() {
        }

        public ExceptionResponseBuilder status(HttpStatus status) {
            this.status = status;
            return this;
        }

        public ExceptionResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public ErrorResponse build() {
            return new ErrorResponse(status, message);
        }
    }
}