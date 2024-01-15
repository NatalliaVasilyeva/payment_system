package com.proselyte.fakepaymentprovider.infrastructure.config;

import com.proselyte.fakepaymentprovider.infrastructure.security.AuthenticationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;

import static org.springframework.security.config.Customizer.withDefaults;

@Slf4j
@Configuration
@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
public class WebSecurityConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, AuthenticationManager authenticationManager) {

        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchanges ->
//                exchanges.pathMatchers("/api/v1/webhook/**").permitAll()
//                    .anyExchange().authenticated())
                exchanges.anyExchange().permitAll())
            .httpBasic(withDefaults())
            .exceptionHandling(handling ->
                handling.authenticationEntryPoint((swe, e) -> {
                        log.error("SecurityWebFilterChain - unauthorized error: {}", e.getMessage());
                        return Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED));
                    })
                    .accessDeniedHandler((swe, e) -> {
                        log.error("SecurityWebFilterChain - access denied: {}", e.getMessage());

                        return Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN));
                    }))
            .addFilterAt(basicAuthenticationFilter(authenticationManager), SecurityWebFiltersOrder.AUTHENTICATION)
            .build();
    }

    @Bean
    public AuthenticationWebFilter basicAuthenticationFilter(AuthenticationManager authenticationManager) {
        AuthenticationWebFilter basicAuthenticationFilter = new AuthenticationWebFilter(authenticationManager);
        basicAuthenticationFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/**"));

        return basicAuthenticationFilter;
    }

}