package com.proselyte.fakepaymentprovider.domain.service;

import com.proselyte.fakepaymentprovider.domain.dto.PayOutRequestDto;
import com.proselyte.fakepaymentprovider.domain.dto.PayOutShotResponseDto;
import com.proselyte.fakepaymentprovider.domain.dto.TransactionResponseDto;
import com.proselyte.fakepaymentprovider.domain.dto.TopUpRequestDto;
import com.proselyte.fakepaymentprovider.domain.dto.TopUpShotResponseDto;
import com.proselyte.fakepaymentprovider.domain.exception.TransactionBadRequestQueryException;
import com.proselyte.fakepaymentprovider.domain.mapper.TransactionMapper;
import com.proselyte.fakepaymentprovider.domain.model.PaymentMessage;
import com.proselyte.fakepaymentprovider.domain.model.PaymentStatus;
import com.proselyte.fakepaymentprovider.domain.model.Transaction;
import com.proselyte.fakepaymentprovider.domain.repository.TransactionRepository;
import com.proselyte.fakepaymentprovider.domain.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    @Autowired
    public PaymentService(TransactionRepository transactionRepository, WalletRepository walletRepository) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
    }


    @Transactional
    public Mono<TopUpShotResponseDto> topUp(TopUpRequestDto topUpRequestDto, UUID merchantId) {
        return Mono.just(topUpRequestDto)
            .map(dto -> TransactionMapper.toTransaction(dto, merchantId))
            .publishOn(Schedulers.boundedElastic())
            .map(transaction -> {
                var wallet = walletRepository.findAllByCurrency(transaction.getCurrency());
                    wallet.subscribe(wl -> {
                        if (wl == null) {
                            transaction.setMessage(PaymentMessage.TRANSACTION_IS_UNSUCCESSFULLY_NO_WALLET_FOR_CURRENCY_FOUND.name());
                            transaction.setStatus(PaymentStatus.FAILED.name());
                            throw new TransactionBadRequestQueryException(PaymentMessage.TRANSACTION_IS_UNSUCCESSFULLY_NO_WALLET_FOR_CURRENCY_FOUND.name());
                        } else {
                            transaction.setMessage(PaymentMessage.OK.name());
                            transaction.setStatus(PaymentStatus.IN_PROGRESS.name());
                        }
                    });
                    return transaction;
            })
            .flatMap(transactionRepository::save)
            .map(TransactionMapper::toTopUpShotResponseDto);
    }

    @Transactional
    public Mono<Transaction> updateTransaction(Transaction transaction) {
        return Mono.just(transaction)
            .flatMap(transactionRepository::save);
    }

    @Transactional
    public Flux<TransactionResponseDto> findAllTransactionsByMerchantIdAndFilter(UUID merchantId, LocalDateTime startDate, LocalDateTime endDate) {

        return transactionRepository.findAllByCreatedAtBetween(startDate, endDate, merchantId)
            .collectList()
            .flatMapIterable(TransactionMapper::toTransactionResponseDtoList);
    }

    @Transactional
    public Flux<Transaction> findAllByTransactional(Boolean transactional) {

        return transactionRepository.findAllByTransactional(transactional);
    }

    @Transactional
    public Mono<TransactionResponseDto> findTransactionById(UUID transactionId) {

        return transactionRepository.findById(transactionId)
            .map(TransactionMapper::toTransactionResponseDto);

    }

    @Transactional
    public Mono<PayOutShotResponseDto> payOut(PayOutRequestDto payOutRequestDto) {

        return Mono.empty();

    }
}