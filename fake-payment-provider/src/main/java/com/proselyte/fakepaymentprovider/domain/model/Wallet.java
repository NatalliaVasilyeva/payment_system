package com.proselyte.fakepaymentprovider.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table("wallet")
public class Wallet implements Persistable<UUID> {

    @Id
    private UUID id;

    private UUID merchantId;

    private String currency;

    private BigDecimal balance;

    @Transient
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return null == getId();
    }
}