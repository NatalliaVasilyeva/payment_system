package com.proselyte.fakepaymentprovider.domain.repository;

import com.proselyte.fakepaymentprovider.domain.model.Transaction;
import jakarta.annotation.Nullable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface TransactionRepository extends R2dbcRepository<Transaction, UUID> {


    @Query("SELECT t FROM Transaction t WHERE (:start is null or t.created_at >= :start) and (:end is null"
        + " or t.created_at <= :end) and  t.merchant_id = :merchantId and transactional = true")
    Flux<Transaction> findAllByCreatedAtBetween(@Nullable LocalDateTime start, @Nullable LocalDateTime end, UUID merchantId);

    @Query("SELECT t FROM Transaction t WHERE transactional = :transactional")
    Flux<Transaction> findAllByTransactional(Boolean transactional);


    Mono<Transaction> findById(UUID id);
}