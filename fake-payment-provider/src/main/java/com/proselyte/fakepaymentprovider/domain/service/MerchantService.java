package com.proselyte.fakepaymentprovider.domain.service;

import com.proselyte.fakepaymentprovider.domain.dto.MerchantRequestDto;
import com.proselyte.fakepaymentprovider.domain.dto.MerchantResponseDto;
import com.proselyte.fakepaymentprovider.domain.mapper.MerchantMapper;
import com.proselyte.fakepaymentprovider.domain.model.SecurityUserDetails;
import com.proselyte.fakepaymentprovider.domain.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantService implements ReactiveUserDetailsService {

    private final MerchantRepository merchantRepository;

    public Mono<MerchantResponseDto> createMerchant(MerchantRequestDto dto) {
        return Mono.just(dto)
            .map(MerchantMapper::toMerchant)
            .flatMap(merchantRepository::save)
            .map(MerchantMapper::toMerchantResponseDto)
            .doOnSuccess(u -> log.info("Merchant service - merchant: {} was created", u));
    }

    @Override
    public Mono<UserDetails> findByUsername(String clientId) {

        return merchantRepository.findByClientId(clientId)
            .filter(Objects::nonNull)
            .map(client -> new SecurityUserDetails(
                client.getClientId(),
                Arrays.toString(client.getClientSecret()),
                Collections.emptyList(),
                client.getId()))
            .switchIfEmpty(Mono.error(() -> new UsernameNotFoundException("Client with such clientId not found!")))
            .cast(UserDetails.class);
    }
}