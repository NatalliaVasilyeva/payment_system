package com.proselyte.fakepaymentprovider.infrastructure.util;

import reactor.core.publisher.Mono;

public class ReactiveContextHolder {
    public static final String MERCHANT = "merchant";

    private ReactiveContextHolder() {
    }

    public static Mono<Object> getContextValueByKey(String key) {
        return Mono.deferContextual(ctx -> Mono.justOrEmpty(ctx.get(key)));
    }

    public static Mono<String> getMerchantClientId() {
        return Mono.deferContextual(ctx -> Mono.justOrEmpty(ctx.get(MERCHANT))).cast(String.class);
    }

}