package com.proselyte.fakepaymentprovider.domain.service;

import com.proselyte.fakepaymentprovider.domain.dto.WebhookRequestDto;
import com.proselyte.fakepaymentprovider.domain.exception.NotificationException;
import com.proselyte.fakepaymentprovider.domain.mapper.WebhookMapper;
import com.proselyte.fakepaymentprovider.infrastructure.connector.NotificationConnector;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.HashMap;

@Slf4j
@AllArgsConstructor
@Service
public class NotificationService {

    private final NotificationConnector connector;
    private final WebhookService webhookService;


    public Mono<WebhookRequestDto> notify(WebhookRequestDto webhookRequestDto) {
        var headers = new HashMap<String, String>();
        return connector.sentNotificationRequest(webhookRequestDto.notificationUrl(), headers, WebhookMapper.toWebhookShotRequestDto(webhookRequestDto))
            .thenReturn(webhookRequestDto)
            .retryWhen(Retry.backoff(3, Duration.ofMillis(10))
                .jitter(0d)
                .doAfterRetry(retrySignal -> {
                    webhookService.createWebhook(WebhookMapper.toWebhook(webhookRequestDto, (int) retrySignal.totalRetries() + 2, "UNSUCCESSFULLY")).subscribe();
                    log.info("Retried " + retrySignal.totalRetries());
                })
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal)
                    -> new NotificationException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception occurred while making notification")))
            .doOnError(error -> log.info("Exception occurred while making notification", error));
    }
}