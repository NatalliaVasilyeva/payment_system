package com.proselyte.fakepaymentprovider.infrastructure.connector;

import com.proselyte.fakepaymentprovider.domain.dto.WebhookShotRequestDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class NotificationConnector extends Connector {

    public NotificationConnector(WebClient webClient) {
        super(webClient);
    }


    public Mono<Void> sentNotificationRequest(String url,
                                              Map<String, String> headers,
                                              WebhookShotRequestDto body) {

        return sentPostRequest(url, headers, body)
            .bodyToMono(Void.class);
    }
}