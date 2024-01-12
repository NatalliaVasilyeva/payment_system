package com.proselyte.fakepaymentprovider.infrastructure.config;

import com.proselyte.fakepaymentprovider.infrastructure.util.ReactiveContextHolder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ReactiveMerchantContextFilter implements WebFilter {


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        return Mono.justOrEmpty(exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION))
            .flatMap(authValue -> Mono.justOrEmpty(authValue.substring("basic ".length())))
            .map(credentials ->
                Arrays.stream(new String(base64Decode(credentials), StandardCharsets.UTF_8)
                            .split(":"))
                        .findFirst()
            )
            .flatMap(merchant -> chain.filter(exchange).contextWrite(ctx -> ctx.put(ReactiveContextHolder.MERCHANT, merchant)));

    }

    private byte[] base64Decode(String value) {
        try {
            return Base64.getDecoder().decode(value);
        } catch (Exception var3) {
            return new byte[0];
        }
    }
}