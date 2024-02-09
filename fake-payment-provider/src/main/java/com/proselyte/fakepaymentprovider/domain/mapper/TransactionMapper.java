package com.proselyte.fakepaymentprovider.domain.mapper;

import com.proselyte.fakepaymentprovider.domain.dto.PayOutRequestDto;
import com.proselyte.fakepaymentprovider.domain.dto.PayOutShotResponseDto;
import com.proselyte.fakepaymentprovider.domain.dto.PayoutResponseDto;
import com.proselyte.fakepaymentprovider.domain.dto.TopUpRequestDto;
import com.proselyte.fakepaymentprovider.domain.dto.TopUpShotResponseDto;
import com.proselyte.fakepaymentprovider.domain.dto.TransactionResponseDto;
import com.proselyte.fakepaymentprovider.domain.model.PaymentMessage;
import com.proselyte.fakepaymentprovider.domain.model.PaymentMethod;
import com.proselyte.fakepaymentprovider.domain.model.PaymentStatus;
import com.proselyte.fakepaymentprovider.domain.entity.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TransactionMapper {

    private TransactionMapper() {
    }

    public static TransactionResponseDto toTransactionResponseDto(Transaction transaction) {
        return new TransactionResponseDto(
            transaction.getId(),
            PaymentMethod.valueOf(transaction.getPaymentMethod()),
            transaction.getAmount(),
            transaction.getCurrency(),
            transaction.getMerchantTransactionId(),
            transaction.getCreatedAt(),
            transaction.getUpdatedAt(),
            transaction.getNotificationUrl(),
            CardMapper.toCardResponseDto(transaction),
            transaction.getLanguage(),
            CustomerMapper.toCustomerResponseDto(transaction),
            PaymentStatus.valueOf(transaction.getStatus()),
            PaymentMessage.valueOf(transaction.getMessage())
        );
    }

    public static PayoutResponseDto toPayoutResponseDto(Transaction transaction) {
        return new PayoutResponseDto(
            transaction.getId(),
            PaymentMethod.valueOf(transaction.getPaymentMethod()),
            transaction.getAmount(),
            transaction.getCurrency(),
            transaction.getMerchantTransactionId(),
            transaction.getCreatedAt(),
            transaction.getUpdatedAt(),
            transaction.getNotificationUrl(),
           "transaction".equals(transaction.getType())? CardMapper.toCardResponseDto(transaction) : CardMapper.toCardResponseHideDto(transaction),
            transaction.getLanguage(),
            CustomerMapper.toCustomerResponseDto(transaction),
            PaymentStatus.valueOf(transaction.getStatus()),
            PaymentMessage.valueOf(transaction.getMessage())
        );
    }

    public static TopUpShotResponseDto toTopUpShotResponseDto(Transaction transaction) {
        return new TopUpShotResponseDto(
            transaction.getId(),
            PaymentStatus.valueOf(transaction.getStatus()),
            PaymentMessage.valueOf(transaction.getMessage())
        );
    }

    public static PayOutShotResponseDto toPayOutShotResponseDto(Transaction transaction) {
        return new PayOutShotResponseDto(
            transaction.getId(),
            PaymentStatus.valueOf(transaction.getStatus()),
            PaymentMessage.valueOf(transaction.getMessage())
        );
    }

    public static List<TransactionResponseDto> toTransactionResponseDtoList(List<Transaction> transactions) {
        return transactions.stream().map(TransactionMapper::toTransactionResponseDto).toList();
    }

    public static List<PayoutResponseDto> toPayoutResponseDtoList(List<Transaction> transactions) {
        return transactions.stream().map(TransactionMapper::toPayoutResponseDto).toList();
    }

    public static Transaction toTransaction(PayOutRequestDto dto, UUID merchantId) {
        return Transaction.builder()
            .merchantId(merchantId)
            .paymentMethod(dto.paymentMethod())
            .amount(dto.amount())
            .currency(dto.currency())
            .merchantTransactionId(dto.companyTransactionId())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .notificationUrl(dto.notificationUrl())
            .cardNumber(dto.cardData().cardNumber())
            .cardExpirationDate(dto.cardData().expDate())
            .cardCvv(dto.cardData().cvv())
            .language(dto.language())
            .customerFirstName(dto.customer().firstName())
            .customerLastName(dto.customer().lastName())
            .customerCountry(dto.customer().country())
            .type("payout")
            .notificationUrl(dto.notificationUrl())
            .build();
    }

    public static Transaction toTransaction(TopUpRequestDto dto, UUID merchantId) {
        return Transaction.builder()
            .merchantId(merchantId)
            .paymentMethod(dto.paymentMethod())
            .amount(dto.amount())
            .currency(dto.currency())
            .merchantTransactionId(dto.companyTransactionId())
            .createdAt(dto.createdAt())
            .updatedAt(dto.updatedAt())
            .notificationUrl(dto.notificationUrl())
            .cardNumber(dto.cardData().cardNumber())
            .cardExpirationDate(dto.cardData().expDate())
            .cardCvv(dto.cardData().cvv())
            .language(dto.language())
            .customerFirstName(dto.customer().firstName())
            .customerLastName(dto.customer().lastName())
            .customerCountry(dto.customer().country())
            .type("transaction")
            .notificationUrl(dto.notificationUrl())
            .build();
    }
}