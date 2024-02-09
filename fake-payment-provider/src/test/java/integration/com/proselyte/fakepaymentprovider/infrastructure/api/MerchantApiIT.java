package integration.com.proselyte.fakepaymentprovider.infrastructure.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.proselyte.fakepaymentprovider.domain.dto.MerchantResponseDto;
import integration.com.proselyte.fakepaymentprovider.infrastructure.util.TestDataHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MerchantApiIT extends ApiBaseTest {

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    void test200OKShouldCreateMerchant() throws JsonProcessingException {

        var merchantRequestDto = testDataHelper.createMerchantRequestDto();

        final String jsonBody = objectMapper.writeValueAsString(merchantRequestDto);

        webTestClient.post().uri("/api/v1/merchant")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(jsonBody)
            .exchange()
            .expectStatus().isCreated()
            .expectBodyList(MerchantResponseDto.class)
            .hasSize(1)
            .consumeWith(response -> {
                List<MerchantResponseDto> merchants = response.getResponseBody();
                merchants.forEach(m -> {
                    assertNotNull(m.clientId());
                    assertEquals(merchantRequestDto.clientId(), m.clientId());
                });
            });
    }
}