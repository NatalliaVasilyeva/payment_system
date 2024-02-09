package com.proselyte.fakepaymentprovider.domain.service;

import com.proselyte.fakepaymentprovider.domain.dto.PayOutRequestDto;
import com.proselyte.fakepaymentprovider.domain.dto.PayOutShotResponseDto;
import com.proselyte.fakepaymentprovider.domain.dto.TopUpRequestDto;
import com.proselyte.fakepaymentprovider.domain.dto.TopUpShotResponseDto;
import com.proselyte.fakepaymentprovider.domain.mapper.TransactionMapper;
import com.proselyte.fakepaymentprovider.domain.entity.Transaction;
import com.proselyte.fakepaymentprovider.domain.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentService {

    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;
    private final PayoutService payoutService;
    private final TransactionalOperator transactionalOperator;

    public Mono<TopUpShotResponseDto> topUp(TopUpRequestDto topUpRequestDto, UUID merchantId) {
        return transactionalOperator.transactional(Mono.just(topUpRequestDto)
            .map(dto -> TransactionMapper.toTransaction(dto, merchantId))
            .flatMap(transaction -> transactionService.saveSuccessTransaction(transaction)
                .onErrorResume(error -> Mono.defer(() -> Mono.error(error)))
                .map(TransactionMapper::toTopUpShotResponseDto)));
    }

    public Mono<PayOutShotResponseDto> payOut(PayOutRequestDto payOutRequestDto, UUID merchantId) {
        return transactionalOperator.transactional(Mono.just(payOutRequestDto)
            .map(dto -> TransactionMapper.toTransaction(dto, merchantId))
            .flatMap(transaction -> payoutService.saveSuccessPayout(transaction)
                .onErrorResume(error -> Mono.defer(() -> Mono.error(error)))
                .map(TransactionMapper::toPayOutShotResponseDto)));
    }

    public Mono<Transaction> update(Transaction transaction) {
        return transactionalOperator.transactional(
            Mono.just(transaction)
                .flatMap(tr -> {
                    if ("transaction".equals(transaction.getType())) {
                        return transactionService.updateTransaction(transaction);
                    } else {
                        return payoutService.updatePayout(transaction);
                    }
                })
                .map(savedTransaction -> savedTransaction));
    }

    public Flux<Transaction> findAllInProgressByType(String type) {
        return transactionRepository.findAllInProgressByType(type);
    }
}