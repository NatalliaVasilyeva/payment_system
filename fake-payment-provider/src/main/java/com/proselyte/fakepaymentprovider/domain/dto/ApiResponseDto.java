package com.proselyte.fakepaymentprovider.domain.dto;

public class ApiResponseDto<T> {

    private T transaction_list;

    public ApiResponseDto() {
        // base default constructor
    }

    public ApiResponseDto(T results) {
        this.transaction_list = transaction_list;
    }

    public T getResults() {
        return transaction_list;
    }

}