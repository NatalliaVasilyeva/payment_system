package com.proselyte.fakepaymentprovider.domain.service;

import com.proselyte.fakepaymentprovider.domain.dto.PayOutRequestDto;
import com.proselyte.fakepaymentprovider.domain.dto.PayOutShotResponseDto;
import com.proselyte.fakepaymentprovider.domain.dto.TopUpRequestDto;
import com.proselyte.fakepaymentprovider.domain.dto.TopUpShotResponseDto;
import com.proselyte.fakepaymentprovider.domain.dto.TransactionResponseDto;
import com.proselyte.fakepaymentprovider.domain.exception.TransactionBadRequestQueryException;
import com.proselyte.fakepaymentprovider.domain.mapper.TransactionMapper;
import com.proselyte.fakepaymentprovider.domain.model.PaymentMessage;
import com.proselyte.fakepaymentprovider.domain.model.PaymentStatus;
import com.proselyte.fakepaymentprovider.domain.model.Transaction;
import com.proselyte.fakepaymentprovider.domain.repository.TransactionRepository;
import com.proselyte.fakepaymentprovider.domain.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final TransactionalOperator transactionalOperator;


    public Mono<Transaction> saveSuccessTransaction(Transaction transaction) {
        return walletRepository.findAllByCurrencyAndMerchantId(transaction.getCurrency(), transaction.getMerchantId())
            .switchIfEmpty(Mono.defer(() -> Mono.error(new TransactionBadRequestQueryException(PaymentMessage.TRANSACTION_IS_UNSUCCESSFULLY_NO_WALLET_FOR_CURRENCY_FOUND.name()))))
            .flatMap(wallet -> {
                transaction.setMessage(PaymentMessage.OK.name());
                transaction.setStatus(PaymentStatus.IN_PROGRESS.name());
                return transactionRepository.save(transaction);
            })
            .map(savedTransaction -> savedTransaction);
    }

    public Mono<Transaction> save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Mono<Transaction> updateTransaction(Transaction transaction) {
        return walletRepository.findAllByCurrencyAndMerchantId(transaction.getCurrency(), transaction.getMerchantId())
            .flatMap(wallet -> {
                if (PaymentStatus.isUnsuccessfullState(transaction.getStatus())) {
                    transaction.setMessage(PaymentMessage.PAYMENT_FAILED.name());
                    transaction.setStatus(transaction.getStatus());
                    return transactionRepository.save(transaction);
                } else {
                    wallet.setBalance(transaction.getAmount());
                    return walletRepository.save(wallet)
                        .flatMap(savedWallet -> {
                            transaction.setMessage(PaymentMessage.OK.name());
                            transaction.setStatus(PaymentStatus.IN_PROGRESS.name());
                            return transactionRepository.save(transaction);
                        });
                }
            })
            .map(savedTransaction -> savedTransaction);
    }

    public Flux<TransactionResponseDto> findAllTransactionsByMerchantIdAndFilter(UUID
                                                                                     merchantId, LocalDateTime
                                                                                     startDate, LocalDateTime endDate) {
        return transactionRepository.findAllByCreatedAtBetween(startDate, endDate, merchantId)
            .collectList()
            .flatMapIterable(TransactionMapper::toTransactionResponseDtoList);
    }

    public Flux<Transaction> findAllByTransactional(Boolean transactional) {

        return transactionRepository.findAllByTransactional(transactional);
    }

    public Mono<TransactionResponseDto> findTransactionById(UUID transactionId) {

        return transactionRepository.findById(transactionId)
            .map(TransactionMapper::toTransactionResponseDto);
    }

    public Mono<PayOutShotResponseDto> payOut(PayOutRequestDto payOutRequestDto) {

        return Mono.empty();
    }
}