package com.proselyte.fakepaymentprovider.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentFilter {

    private LocalDateTime startDate;
    private LocalDateTime endDate;
}