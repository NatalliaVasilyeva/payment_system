package com.proselyte.fakepaymentprovider.infrastructure.security;


import com.proselyte.fakepaymentprovider.domain.exception.DomainResponseException;
import com.proselyte.fakepaymentprovider.domain.model.SecurityUserDetails;
import com.proselyte.fakepaymentprovider.domain.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final MerchantService merchantService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        var principal = (SecurityUserDetails) authentication.getPrincipal();
        return merchantService.findByUsername(principal.getUsername())
            .switchIfEmpty(Mono.error(new DomainResponseException(HttpStatus.BAD_REQUEST, "Client does not exist")))
            .handle((user, sink) -> {
                if (!passwordEncoder.matches(principal.getPassword(), user.getPassword())) {
                    sink.error(new BadCredentialsException("Wrong client secret"));
                }
            })
            .contextWrite(Context.of("MERCHANT_ID", principal.getUsername()))
            .map(user -> authentication);
    }
}