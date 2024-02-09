package com.proselyte.fakepaymentprovider.infrastructure.util;

import com.proselyte.fakepaymentprovider.domain.dto.DateFilterDto;
import com.proselyte.fakepaymentprovider.domain.model.PaymentFilter;

import java.time.LocalDate;

public class PaymentFilterUtil {

    private PaymentFilterUtil() {
    }

    public static PaymentFilter createPaymentFilter(DateFilterDto dateFilterDto) {
        var paymentFilter = new PaymentFilter();
        if (dateFilterDto.startDate() == null && dateFilterDto.endDate() == null) {
            paymentFilter.setStartDate(LocalDate.now().atStartOfDay());
            paymentFilter.setEndDate(LocalDate.now().atTime(23, 59, 59));
        } else {
            paymentFilter.setStartDate(dateFilterDto.startDate());
            paymentFilter.setEndDate(dateFilterDto.endDate());
        }
        return paymentFilter;
    }
}