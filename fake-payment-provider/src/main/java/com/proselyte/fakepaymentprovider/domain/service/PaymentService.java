package com.proselyte.fakepaymentprovider.domain.service;

import com.proselyte.fakepaymentprovider.domain.dto.PayOutRequestDto;
import com.proselyte.fakepaymentprovider.domain.dto.PayOutShotResponseDto;
import com.proselyte.fakepaymentprovider.domain.dto.TopUpRequestDto;
import com.proselyte.fakepaymentprovider.domain.dto.TopUpShotResponseDto;
import com.proselyte.fakepaymentprovider.domain.mapper.TransactionMapper;
import com.proselyte.fakepaymentprovider.domain.mapper.WebhookMapper;
import com.proselyte.fakepaymentprovider.domain.model.PaymentMessage;
import com.proselyte.fakepaymentprovider.domain.model.PaymentStatus;
import com.proselyte.fakepaymentprovider.domain.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentService {

    private final TransactionService transactionService;
    private final WebhookService webhookService;
    private final TransactionalOperator transactionalOperator;
    private final NotificationService notificationService;


    public Mono<TopUpShotResponseDto> topUp(TopUpRequestDto topUpRequestDto, UUID merchantId) {
        return transactionalOperator.transactional(Mono.just(topUpRequestDto)
            .map(dto -> TransactionMapper.toTransaction(dto, merchantId))
            .flatMap(transaction -> transactionService.saveSuccessTransaction(transaction)
                .onErrorResume(error -> {
                    transaction.setMessage(PaymentMessage.TRANSACTION_IS_UNSUCCESSFULLY_NO_WALLET_FOR_CURRENCY_FOUND.name());
                    transaction.setStatus(PaymentStatus.FAILED.name());
                    return transactionService.save(transaction)
                        .flatMap(savedTransaction -> webhookService.saveWebhook(WebhookMapper.toWebhook(savedTransaction))
                            .flatMap(notificationService::notify)
                            .onErrorResume(err -> {
                                return Mono.defer(Mono::empty);
                            }))
//                            .then(Mono.fromCallable(() -> savedTransaction)))
                        .then(Mono.defer(() -> Mono.error(error)));
                })
                .flatMap(savedTransaction -> webhookService.saveWebhook(WebhookMapper.toWebhook(savedTransaction))
                    .flatMap(notificationService::notify)
                    .then(Mono.fromCallable(() -> savedTransaction)))
                .map(TransactionMapper::toTopUpShotResponseDto)));
    }

    public Mono<Transaction> update(Transaction transaction) {
        return transactionalOperator.transactional(transactionService.updateTransaction(transaction)
            .flatMap(savedTransaction -> webhookService.saveWebhook(WebhookMapper.toWebhook(savedTransaction))
                .flatMap(notificationService::notify)
                .then(Mono.just(savedTransaction))));
    }

    public Mono<PayOutShotResponseDto> payOut(PayOutRequestDto payOutRequestDto) {

        return Mono.empty();
    }
}