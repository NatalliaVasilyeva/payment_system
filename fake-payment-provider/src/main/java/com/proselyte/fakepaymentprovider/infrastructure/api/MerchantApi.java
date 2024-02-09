package com.proselyte.fakepaymentprovider.infrastructure.api;

import com.proselyte.fakepaymentprovider.domain.dto.MerchantRequestDto;
import com.proselyte.fakepaymentprovider.domain.dto.MerchantResponseDto;
import com.proselyte.fakepaymentprovider.domain.service.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/")
@Validated
public class MerchantApi {

    private static final String MERCHANT_URL_NAME = "merchant";

    private final MerchantService merchantService;


    @PostMapping(value = MERCHANT_URL_NAME, consumes = MediaType.APPLICATION_JSON_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create merchant")
    public Mono<MerchantResponseDto> createMerchant(@Parameter(required = true) @RequestBody MerchantRequestDto dto) {
        return merchantService.createMerchant(dto);
    }
}