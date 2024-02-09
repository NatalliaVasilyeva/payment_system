package com.proselyte.fakepaymentprovider.domain.dto;

import java.util.List;

public class ApiResponseDto<T> {

    private List<T> transactionList;

    public ApiResponseDto() {
        // base default constructor
    }

    public ApiResponseDto(List<T> transactionList) {
        this.transactionList = transactionList;
    }

    public List<T> getTransactionList() {
        return transactionList;
    }

}