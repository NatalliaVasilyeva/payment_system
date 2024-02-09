package com.proselyte.fakepaymentprovider.domain.repository;

import com.proselyte.fakepaymentprovider.domain.entity.Transaction;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;
import reactor.util.annotation.Nullable;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface TransactionRepository extends R2dbcRepository<Transaction, UUID> {


    @Query("SELECT * FROM Transaction t WHERE (:start is null or t.created_at >= :start) and (:end is null"
        + " or t.created_at <= :end) and t.merchant_id = :merchantId and t.type = :type")
    Flux<Transaction> findAllByPaymentFilterAndType(@Nullable LocalDateTime start, @Nullable LocalDateTime end, UUID merchantId, String type);

    @Query("SELECT * FROM Transaction t WHERE t.status = 'IN_PROGRESS' AND t.type = :type")
    Flux<Transaction> findAllInProgressByType(String type);

    Mono<Transaction> findById(@NonNull UUID id);
}