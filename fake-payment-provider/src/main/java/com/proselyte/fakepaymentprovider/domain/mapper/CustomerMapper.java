package com.proselyte.fakepaymentprovider.domain.mapper;

import com.proselyte.fakepaymentprovider.domain.dto.CustomerResponseDto;
import com.proselyte.fakepaymentprovider.domain.model.Transaction;

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
}