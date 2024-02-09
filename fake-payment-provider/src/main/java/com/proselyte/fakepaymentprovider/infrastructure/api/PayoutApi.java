package com.proselyte.fakepaymentprovider.infrastructure.api;

import com.proselyte.fakepaymentprovider.domain.dto.ApiResponseDto;
import com.proselyte.fakepaymentprovider.domain.dto.DateFilterDto;
import com.proselyte.fakepaymentprovider.domain.dto.PayOutRequestDto;
import com.proselyte.fakepaymentprovider.domain.dto.PayOutShotResponseDto;
import com.proselyte.fakepaymentprovider.domain.dto.PayoutResponseDto;
import com.proselyte.fakepaymentprovider.domain.exception.DomainResponseException;
import com.proselyte.fakepaymentprovider.domain.model.PaymentMessage;
import com.proselyte.fakepaymentprovider.domain.model.PaymentMethod;
import com.proselyte.fakepaymentprovider.domain.model.SecurityUserDetails;
import com.proselyte.fakepaymentprovider.domain.service.PaymentService;
import com.proselyte.fakepaymentprovider.domain.service.PayoutService;
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
import java.util.UUID;


@Tag(name = "Payout API", description = "Payout API")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/")
@Validated
public class PayoutApi {

    public static final String PAYOUT_URL_NAME = "payments/payout";

    private final PaymentService paymentService;
    private final PayoutService payoutService;


    @PostMapping(value = PAYOUT_URL_NAME, consumes = MediaType.APPLICATION_JSON_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create payout")
    public Mono<PayOutShotResponseDto> payout(@AuthenticationPrincipal SecurityUserDetails user,
                                              @Valid @RequestBody PayOutRequestDto payOutRequestDto) {

        // input validation
        var paymentMethod = payOutRequestDto.paymentMethod();
        var amount = payOutRequestDto.amount();

        if (!PaymentMethod.isValidPaymentMethod(paymentMethod)) {
            throw new DomainResponseException(HttpStatus.BAD_REQUEST, PaymentMessage.PAYMENT_METHOD_NOT_ALLOWED.name());
        }

        if (amount.equals(BigDecimal.ZERO)) {
            throw new DomainResponseException(HttpStatus.EXPECTATION_FAILED, PaymentMessage.PAYOUT_MIN_AMOUNT.name());
        }

        return paymentService.payOut(payOutRequestDto, user.getId());

    }

    @GetMapping(value = PAYOUT_URL_NAME + "/list", consumes = MediaType.APPLICATION_JSON_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get list of payouts for merchant. Can be filtered by payout start time")
    public Mono<ApiResponseDto<PayoutResponseDto>> getPayouts(@AuthenticationPrincipal SecurityUserDetails user,
                                                DateFilterDto dateFilterDto) {
        return payoutService.findAllPayoutsByMerchantIdAndFilter(user.getId(), dateFilterDto)
            .collectList()
            .map(ApiResponseDto::new);

    }

    @GetMapping(value = PAYOUT_URL_NAME + "/{uuid}" + "/details", consumes = MediaType.APPLICATION_JSON_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get detail information about payout")
    public Mono<PayoutResponseDto> getPayoutDetails(@Parameter(required = true) @PathVariable @Valid @NotNull UUID uuid) {

        return payoutService.findPayoutById(uuid);

    }

}