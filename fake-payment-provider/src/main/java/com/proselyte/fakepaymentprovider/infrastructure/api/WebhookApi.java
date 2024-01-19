package com.proselyte.fakepaymentprovider.infrastructure.api;

import com.proselyte.fakepaymentprovider.domain.service.WebhookExecutionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/")
@Validated
public class WebhookApi {

    private static final String TRANSACTION_WEBHOOK_URL_NAME = "webhook/transaction";
    private static final String PAYOUT_WEBHOOK_URL_NAME = "webhook/payout";

    private final WebhookExecutionService webhookService;


    @PostMapping(value = TRANSACTION_WEBHOOK_URL_NAME, consumes = MediaType.APPLICATION_JSON_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Sent notification about transaction operations")
    public void handleTransactionWebhookData() {

        webhookService.executePaymentWebhook(true);
    }

    @PostMapping(value = PAYOUT_WEBHOOK_URL_NAME, consumes = MediaType.APPLICATION_JSON_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Sent notification about transaction operations")
    public void handlePayoutWebhookData() {

        webhookService.executePaymentWebhook(false);
    }
}