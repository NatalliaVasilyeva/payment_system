package com.proselyte.fakepaymentprovider.domain.service;

import com.proselyte.fakepaymentprovider.domain.model.Webhook;
import com.proselyte.fakepaymentprovider.domain.repository.WebhookRepository;
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
    private final TransactionService transactionService;
    private final WebhookRepository webhookRepository;

    public Mono<Void> executePaymentWebhook(boolean transactional) {
        return transactionService.findAllByTransactional(transactional)
            .switchIfEmpty(Mono.defer(() -> {
                log.debug("No any transactions is present");
                return Mono.empty();
            }))
            .flatMap(tr -> {
                if (transactional) {
                    tr.setStatus(Generator.generateRandomTransactionStatus().name());
                } else {
                    tr.setStatus(Generator.generateRandomPayoutStatus().name());
                }
                return paymentService.update(tr);
            })
            .then();
    }

    public Mono<Webhook> saveWebhook(Webhook webhook) {
        return Mono.just(webhook)
            .flatMap(webhookRepository::save);
    }
}