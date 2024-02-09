package com.proselyte.fakepaymentprovider.domain.service;

import com.proselyte.fakepaymentprovider.domain.entity.Webhook;
import com.proselyte.fakepaymentprovider.domain.repository.WebhookRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@AllArgsConstructor
@Service
public class WebhookService {

    private final WebhookRepository webhookRepository;

    public Mono<Webhook> createWebhook(Webhook webhook) {
        return Mono.just(webhook)
            .flatMap(webhookRepository::save);
    }
}