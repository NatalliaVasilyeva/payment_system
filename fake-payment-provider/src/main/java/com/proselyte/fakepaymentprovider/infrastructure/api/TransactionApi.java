package com.proselyte.fakepaymentprovider.infrastructure.api;

import com.proselyte.fakepaymentprovider.domain.dto.ApiResponseDto;
import com.proselyte.fakepaymentprovider.domain.dto.DateFilterDto;
import com.proselyte.fakepaymentprovider.domain.dto.TransactionResponseDto;
import com.proselyte.fakepaymentprovider.domain.dto.TopUpRequestDto;
import com.proselyte.fakepaymentprovider.domain.dto.TopUpShotResponseDto;
import com.proselyte.fakepaymentprovider.domain.exception.DomainResponseException;
import com.proselyte.fakepaymentprovider.domain.model.PaymentMessage;
import com.proselyte.fakepaymentprovider.domain.model.PaymentMethod;
import com.proselyte.fakepaymentprovider.domain.model.SecurityUserDetails;
import com.proselyte.fakepaymentprovider.domain.service.PaymentService;
import com.proselyte.fakepaymentprovider.domain.service.TransactionService;
import com.proselyte.fakepaymentprovider.infrastructure.util.Validator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.UUID;


@Tag(name = "Transaction API", description = "Transaction API")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/")
@Validated
public class TransactionApi {

    public static final String TRANSACTION_URL_NAME = "payments/transaction";

    private final PaymentService paymentService;
    private final TransactionService transactionService;

    @PostMapping(value = TRANSACTION_URL_NAME, consumes = MediaType.APPLICATION_JSON_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create top up transaction")
    public Mono<TopUpShotResponseDto> topUp(@AuthenticationPrincipal SecurityUserDetails user,
                                            @Valid @RequestBody TopUpRequestDto topUpRequestDto) throws ParseException {

        // input validation
        var paymentMethod = topUpRequestDto.paymentMethod();
        var amount = topUpRequestDto.amount();

        if (!PaymentMethod.isValidPaymentMethod(paymentMethod)) {
            throw new DomainResponseException(HttpStatus.BAD_REQUEST, PaymentMessage.PAYMENT_METHOD_NOT_ALLOWED.name());
        }

        if (amount.equals(BigDecimal.ZERO) || BigDecimal.valueOf(0L).equals(amount)) {
            throw new DomainResponseException(HttpStatus.BAD_REQUEST, PaymentMessage.TRANSACTION_MIN_AMOUNT.name());
        }

       if (topUpRequestDto.cardData().expDate() != null) {
           Validator.validateCardExpirationDate(topUpRequestDto.cardData().expDate());
       } else {
           throw new DomainResponseException(HttpStatus.BAD_REQUEST, "CVV must present");
       }

        return paymentService.topUp(topUpRequestDto, user.getId());

    }

    @GetMapping(value = TRANSACTION_URL_NAME + "/list", consumes = MediaType.APPLICATION_JSON_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get list of transactions for merchant. Can be filtered by transaction start time")
    public Mono<ApiResponseDto<TransactionResponseDto>> getTransactions(@AuthenticationPrincipal SecurityUserDetails user,
                                                        DateFilterDto dateFilterDto) {
        return transactionService.findAllTransactionsByMerchantIdAndFilter(user.getId(), dateFilterDto)
            .collectList()
            .map(ApiResponseDto::new);

    }

    @GetMapping(value = TRANSACTION_URL_NAME + "/{uuid}" + "/details", consumes = MediaType.APPLICATION_JSON_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get detail information about transaction")
    public Mono<TransactionResponseDto> getTransactionDetails(@Parameter(required = true) @PathVariable @Valid @NotNull UUID uuid) {
       return transactionService.findTransactionById(uuid);

    }

}