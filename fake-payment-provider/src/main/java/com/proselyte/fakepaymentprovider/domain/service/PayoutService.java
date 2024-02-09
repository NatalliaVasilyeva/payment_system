package com.proselyte.fakepaymentprovider.domain.service;

import com.proselyte.fakepaymentprovider.domain.dto.DateFilterDto;
import com.proselyte.fakepaymentprovider.domain.dto.PayoutResponseDto;
import com.proselyte.fakepaymentprovider.domain.exception.PayoutBadRequestQueryException;
import com.proselyte.fakepaymentprovider.domain.exception.TransactionBadRequestQueryException;
import com.proselyte.fakepaymentprovider.domain.mapper.TransactionMapper;
import com.proselyte.fakepaymentprovider.domain.model.PaymentMessage;
import com.proselyte.fakepaymentprovider.domain.model.PaymentStatus;
import com.proselyte.fakepaymentprovider.domain.entity.Transaction;
import com.proselyte.fakepaymentprovider.domain.repository.TransactionRepository;
import com.proselyte.fakepaymentprovider.domain.repository.WalletRepository;
import com.proselyte.fakepaymentprovider.infrastructure.util.PaymentFilterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class PayoutService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;


    public Mono<Transaction> saveSuccessPayout(Transaction transaction) {
        return walletRepository.findAllByCurrencyAndMerchantId(transaction.getCurrency(), transaction.getMerchantId())
            .switchIfEmpty(Mono.defer(() -> Mono.error(new TransactionBadRequestQueryException(PaymentMessage.PAYMENT_IS_UNSUCCESSFULLY_NO_WALLET_FOR_CURRENCY_FOUND.name()))))
            .flatMap(wallet -> {
                transaction.setMessage(PaymentMessage.OK.name());
                transaction.setStatus(PaymentStatus.IN_PROGRESS.name());
                return transactionRepository.save(transaction);
            })
            .map(savedTransaction -> savedTransaction);
    }

    public Mono<Transaction> updatePayout(Transaction transaction) {
        return walletRepository.findAllByCurrencyAndMerchantId(transaction.getCurrency(), transaction.getMerchantId())
            .flatMap(wallet -> {
                if (PaymentStatus.isUnsuccessfullState(transaction.getStatus())) {
                    transaction.setMessage(PaymentMessage.PAYMENT_FAILED.name());
                    transaction.setStatus(transaction.getStatus());
                    return transactionRepository.save(transaction);
                } else {
                    if (wallet.getBalance().compareTo(transaction.getAmount()) < 0) {
                        return Mono.error(new PayoutBadRequestQueryException(PaymentMessage.PAYMENT_IS_UNSUCCESSFULLY_BALANCE_NOT_ENOUGH.name()));
                    }
                    wallet.setBalance(wallet.getBalance().add(transaction.getAmount()));
                    return walletRepository.save(wallet)
                        .flatMap(savedWallet -> {
                            transaction.setMessage(PaymentMessage.OK.name());
                            transaction.setStatus(PaymentStatus.COMPLETED.name());
                            return transactionRepository.save(transaction);
                        });
                }
            })
            .map(savedTransaction -> savedTransaction);
    }

    public Flux<PayoutResponseDto> findAllPayoutsByMerchantIdAndFilter(UUID merchantId,
                                                                       DateFilterDto dateFilterDto) {
        var paymentFilter = PaymentFilterUtil.createPaymentFilter(dateFilterDto);
        return transactionRepository.findAllByPaymentFilterAndType(paymentFilter.getStartDate(), paymentFilter.getEndDate(), merchantId, "payout")
            .switchIfEmpty(Flux.empty())
            .collectList()
            .flatMapIterable(TransactionMapper::toPayoutResponseDtoList);
    }

    public Mono<PayoutResponseDto> findPayoutById(UUID payoutId) {
        return transactionRepository.findById(payoutId)
            .map(TransactionMapper::toPayoutResponseDto);
    }

}