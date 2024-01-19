//package com.proselyte.fakepaymentprovider.infrastructure.security;
//
//
//import com.proselyte.fakepaymentprovider.domain.exception.DomainResponseException;
//import com.proselyte.fakepaymentprovider.domain.service.MerchantService;
//import com.proselyte.fakepaymentprovider.infrastructure.util.ReactiveContextHolder;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.ReactiveAuthenticationManager;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Component;
//import reactor.core.publisher.Mono;
//
//@Component
//@RequiredArgsConstructor
//public class AuthenticationManager implements ReactiveAuthenticationManager {
//
//    private final MerchantService merchantService;
//    private final BCryptPasswordEncoder passwordEncoder;
//
//    @Override
//    public Mono<Authentication> authenticate(Authentication authentication) {
//        var principal = (String) authentication.getPrincipal();
//        var password = (String) authentication.getCredentials();
//        return merchantService.findByUsername(principal)
//            .switchIfEmpty(Mono.error(new DomainResponseException(HttpStatus.BAD_REQUEST, "Client does not exist")))
//            .map(user -> {
//                if (!passwordEncoder.matches(password, user.getPassword())) {
//                    Mono.error(new DomainResponseException(HttpStatus.BAD_REQUEST, "Wrong client secret"));
//                }
//                return user;
//            })
//            .contextWrite(ctx -> ctx.put(ReactiveContextHolder.MERCHANT, principal))
//            .map(user -> authentication);
//    }
//}