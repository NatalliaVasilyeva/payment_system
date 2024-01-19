package com.proselyte.fakepaymentprovider.infrastructure.connector;

import com.proselyte.fakepaymentprovider.domain.exception.NotificationException;
import com.proselyte.fakepaymentprovider.domain.model.Webhook;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
                                              Webhook body) {

        return sentPostRequest(url, headers, body)
//            .onStatus(HttpStatusCode::isError, clientResponse -> Mono.error(new NotificationException(HttpStatus.INTERNAL_SERVER_ERROR, "Notification was not delivered successfully")))
            .bodyToMono(Void.class);
    }
}