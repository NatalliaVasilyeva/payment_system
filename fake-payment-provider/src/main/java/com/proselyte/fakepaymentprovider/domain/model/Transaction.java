package com.proselyte.fakepaymentprovider.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table("transaction")
public class Transaction implements Persistable<UUID> {

    @Id
    private UUID id;

    private UUID merchantId;

    private String paymentMethod;

    private BigDecimal amount;

    private String currency;

    private UUID merchantTransactionId;

    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private String cardNumber;

    private String cardExpirationDate;

    private byte[] cardCvv;

    private String language;

    private String customerFirstName;

    private String customerLastName;

    private String customerCountry;

    private boolean transactional;

    private String status;

    private  String message;

    @Transient
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return null == getId();
    }
}