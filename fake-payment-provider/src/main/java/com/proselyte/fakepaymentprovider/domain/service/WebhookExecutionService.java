package com.proselyte.fakepaymentprovider.domain.service;

import com.proselyte.fakepaymentprovider.domain.mapper.WebhookMapper;
import com.proselyte.fakepaymentprovider.infrastructure.util.Generator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@AllArgsConstructor
@Service
public class WebhookExecutionService {

    private final PaymentService paymentService;
    private final NotificationService notificationService;
    private final WebhookService webhookService;


    public Mono<Void> executePaymentWebhook(String type) {
        return paymentService.findAllInProgressByType(type)
            .switchIfEmpty(Mono.defer(() -> {
                log.warn("No any payments are present");
                return Mono.empty();
            }))
            .flatMap(tr -> {
                log.info("Check payment status");
                if ("transaction".equals(type)) {
                    log.info("Transaction start to change it status");
                    tr.setStatus(Generator.generateRandomTransactionStatus().name());
                } else {
                    log.info("Payout start to change it status");
                    tr.setStatus(Generator.generateRandomPayoutStatus().name());
                }
                return paymentService.update(tr)
                    .flatMap(transaction -> {
                        log.info("Notification calls");
                        var webhookRequest = WebhookMapper.toWebhookRequestDto(transaction);
                        return notificationService.notify(webhookRequest)
                            .doOnSuccess(notifiedWebhook -> log.info("webhook {} was sent", webhookRequest))
                            .onErrorResume(error -> webhookService.createWebhook(WebhookMapper.toWebhook(webhookRequest, 1, "UNSUCCESSFULLY"))
                                .map(response -> response)
                                .then(Mono.error(error)))
                            .flatMap(response -> webhookService.createWebhook(WebhookMapper.toWebhook(webhookRequest, 1, "SUCCESSFULLY")));
                    });
            })
            .then();
    }
}