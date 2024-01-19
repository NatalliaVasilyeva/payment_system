package com.proselyte.fakepaymentprovider.domain.service;

import com.proselyte.fakepaymentprovider.domain.exception.NotificationException;
import com.proselyte.fakepaymentprovider.domain.model.Webhook;
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

    public Mono<Void> notify(Webhook webhook) {
        var headers = new HashMap<String, String>();
        log.info(webhook.toString());
        return connector.sentNotificationRequest(webhook.getNotificationUrl(), headers, webhook)
//            .onErrorResume(NotificationException.class, ex -> {
//                log.error("Exception occurred while making notification");
//                return Mono.error(ex);
//            })
            .retryWhen(Retry.backoff(3, Duration.ofMillis(10))
            .jitter(0d)
            .doAfterRetry(retrySignal -> {
                log.info("Retried " + retrySignal.totalRetries());
            })
            .onRetryExhaustedThrow((retryBackoffSpec, retrySignal)
                -> new NotificationException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception occurred while making notification")))

//            .retryWhen(Retry.maxInARow(3)
//                .doAfterRetry(response -> log.info("Retry call was sent")))
            .doOnSuccess(clientResponse -> log.info("Notification delivered successfully"))
            .doOnError(error ->
                log.info("Exception occurred while making notification", error)
            )
            .then(Mono.defer(Mono::empty));
    }
}