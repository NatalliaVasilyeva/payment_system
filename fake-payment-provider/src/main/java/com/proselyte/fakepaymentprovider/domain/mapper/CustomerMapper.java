package com.proselyte.fakepaymentprovider.domain.mapper;

import com.proselyte.fakepaymentprovider.domain.dto.CustomerResponseDto;
import com.proselyte.fakepaymentprovider.domain.dto.CustomerShortResponseDto;
import com.proselyte.fakepaymentprovider.domain.entity.Transaction;

public class CustomerMapper {

    private CustomerMapper() {
    }

    public static CustomerResponseDto toCustomerResponseDto(Transaction transaction) {
        return new CustomerResponseDto(
            transaction.getCustomerFirstName(),
            transaction.getCustomerLastName(),
            transaction.getCustomerCountry()
        );
    }

    public static CustomerShortResponseDto toCustomerShotResponseDto(Transaction transaction) {
        return new CustomerShortResponseDto(
            transaction.getCustomerFirstName(),
            transaction.getCustomerLastName()
        );
    }
}