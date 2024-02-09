package com.proselyte.fakepaymentprovider.domain.repository;

import com.proselyte.fakepaymentprovider.domain.entity.Wallet;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface WalletRepository extends R2dbcRepository<Wallet, UUID> {

    Mono<Wallet> findAllByCurrencyAndMerchantId(String currency, UUID merchantId);
}