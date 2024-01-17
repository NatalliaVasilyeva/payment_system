package com.proselyte.fakepaymentprovider.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RequiredArgsConstructor
public class BasicServerAuthenticationConverter implements ServerAuthenticationConverter {

    private static final String BASIC_PREFIX = "basic ";
    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION))
            .flatMap(authValue -> Mono.justOrEmpty(authValue.substring(BASIC_PREFIX.length())))
            .flatMap(credentials -> {
                var parts=  new String(base64Decode(credentials), StandardCharsets.UTF_8)
                        .split(":");
                if (parts.length != 2) {
                    return Mono.empty();
                }
                return Mono.just(new UsernamePasswordAuthenticationToken(parts[0], parts[1]));
            });
    }

    private byte[] base64Decode(String value) {
        try {
            return Base64.getDecoder().decode(value);
        } catch (Exception var3) {
            return new byte[0];
        }
    }
}