package com.proselyte.fakepaymentprovider.infrastructure.connector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Component
public class Connector {

    private final WebClient webClient;

    @Autowired
    public Connector(WebClient webClient) {
        this.webClient = webClient;
    }

    public <T> WebClient.ResponseSpec sentPostRequest(String url,
                                                            Map<String, String> headers,
                                                            T body) {

        return requestPostMethodSpecification(headers)
            .uri(requestURI(url))
            .bodyValue(BodyInserters.fromValue(body))
            .retrieve();
    }


    private WebClient.RequestBodyUriSpec requestPostMethodSpecification(Map<String, String> headers) {
        var request = webClient.post();
        request.accept(MediaType.APPLICATION_JSON);
        request.contentType(MediaType.APPLICATION_JSON);
        if (headers != null) {
            headers.forEach(request::header);
        }
        return request;
    }

    private URI requestURI(String url) {
        return UriComponentsBuilder.fromUriString(url)
            .build().toUri();
    }
}