package com.proselyte.fakepaymentprovider.domain.repository;

import com.proselyte.fakepaymentprovider.domain.model.Merchant;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MerchantRepository extends R2dbcRepository<Merchant, UUID> {

    Mono<Merchant> findByClientId(String clientId);
}