package com.proselyte.fakepaymentprovider.domain.repository;

import com.proselyte.fakepaymentprovider.domain.model.Wallet;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface WalletRepository extends R2dbcRepository<Wallet, UUID> {


//    @Query("SELECT w FROM Wallet w WHERE ww.currency = :currency and w.balance >= :amount)")
    Mono<Wallet> findAllByCurrencyAndBalanceAndMerchantId(String currency, BigDecimal amount, UUID merchantId);

//    @Query("SELECT w FROM Wallet w WHERE w.currency = :currency and w.merhantId = :merchantId)")
    Mono<Wallet> findAllByCurrencyAndMerchantId(String currency, UUID merchantId);
}