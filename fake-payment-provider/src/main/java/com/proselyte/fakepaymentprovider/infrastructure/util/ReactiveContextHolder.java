package com.proselyte.fakepaymentprovider.infrastructure.util;

import reactor.core.publisher.Mono;

import java.util.UUID;

public class ReactiveContextHolder {
    public static final String MERCHANT = "merchant";

    private ReactiveContextHolder() {
    }

    public static Mono<Object> getContextValueByKey(String key) {
        return Mono.deferContextual(ctx -> Mono.justOrEmpty(ctx.get(key)));
    }

    public static Mono<java.util.UUID> getMerchantId() {
        return Mono.deferContextual(ctx -> Mono.justOrEmpty(ctx.get(MERCHANT))).cast(UUID.class);
    }

}