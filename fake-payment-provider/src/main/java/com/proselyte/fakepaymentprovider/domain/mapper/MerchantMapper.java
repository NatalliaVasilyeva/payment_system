package com.proselyte.fakepaymentprovider.domain.mapper;

import com.proselyte.fakepaymentprovider.domain.dto.MerchantRequestDto;
import com.proselyte.fakepaymentprovider.domain.dto.MerchantResponseDto;
import com.proselyte.fakepaymentprovider.domain.model.Merchant;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class MerchantMapper {

    private MerchantMapper() {
    }

    public static MerchantResponseDto toMerchantResponseDto(Merchant merchant) {
        return new MerchantResponseDto(
            merchant.getClientId()
        );
    }

    public static Merchant toMerchant(MerchantRequestDto merchantRequestDto) {
        return new Merchant().toBuilder()
            .clientId(merchantRequestDto.clientId())
            .clientSecret(merchantRequestDto.clientSecret())
            .build();
    }
}