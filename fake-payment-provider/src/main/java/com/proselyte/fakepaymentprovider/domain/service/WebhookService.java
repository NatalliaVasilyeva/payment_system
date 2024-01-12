package com.proselyte.fakepaymentprovider.domain.service;

import com.proselyte.fakepaymentprovider.domain.mapper.WebhookMapper;
import com.proselyte.fakepaymentprovider.domain.model.Webhook;
import com.proselyte.fakepaymentprovider.domain.repository.WebhookRepository;
import com.proselyte.fakepaymentprovider.infrastructure.util.Generator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class WebhookService {

    private final PaymentService paymentService;
    private final WebhookRepository webhookRepository;
    private final WebClient webclient;

    @Autowired
    public WebhookService(PaymentService paymentService,
                          WebClient webclient,
                          WebhookRepository webhookRepository) {
        this.paymentService = paymentService;
        this.webclient = webclient;
        this.webhookRepository = webhookRepository;
    }


    public void executeTransactionWebhook(boolean transactional) {
        paymentService.findAllByTransactional(transactional)
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
                return paymentService.updateTransaction(tr)
                    .flatMap(savedTransaction ->
                        saveWebhook(WebhookMapper.toWebhook(savedTransaction))
                            .flatMap(webhook ->
                                webclient.post()
                                    .uri(webhook.getNotificationUrl())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .bodyValue(BodyInserters.fromValue(webhook))
                                    .retrieve()
                                    .toBodilessEntity())
                    );
            })
            .subscribe();
    }

    public Mono<Webhook> saveWebhook(Webhook webhook) {
        return Mono.just(webhook)
            .flatMap(webhookRepository::save);
    }
}