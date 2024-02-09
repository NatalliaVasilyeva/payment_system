package integration.com.proselyte.fakepaymentprovider.infrastructure.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.proselyte.fakepaymentprovider.domain.dto.ApiResponseDto;
import com.proselyte.fakepaymentprovider.domain.dto.PayOutShotResponseDto;
import com.proselyte.fakepaymentprovider.domain.dto.PayoutResponseDto;
import com.proselyte.fakepaymentprovider.domain.model.PaymentMessage;
import com.proselyte.fakepaymentprovider.domain.model.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.proselyte.fakepaymentprovider.infrastructure.api.PayoutApi.PAYOUT_URL_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

class PayoutApiIT extends ApiBaseTest {

    private static final String PAYOUT_PATH = "/api/v1/" + PAYOUT_URL_NAME;

    @Test
    @WithMockUser(username = "testClientId")
    void test201CreatedShouldCreatePayout() throws JsonProcessingException {
        testDataHelper.prepareDataWithMerchantIdReturn("USD");
        var payOutRequestDto = testDataHelper.createPayOutRequestDto("USD", "CARD", BigDecimal.valueOf(1000), "11/25");

        final String jsonBody = objectMapper.writeValueAsString(payOutRequestDto);

        webTestClient.post().uri(PAYOUT_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(jsonBody)
            .exchange()
            .expectStatus().isCreated()
            .expectBodyList(PayOutShotResponseDto.class)
            .hasSize(1)
            .consumeWith(response -> {
                List<PayOutShotResponseDto> payouts = response.getResponseBody();
                assert payouts != null;
                payouts.forEach(po -> {
                    assertNotNull(po.payoutId());
                    assertEquals(PaymentMessage.OK, po.message());
                    assertEquals(PaymentStatus.IN_PROGRESS, po.status());
                });
            });
    }

    @Test
    @WithMockUser(username = "testClientId")
    void test400BadRequestNotValidPaymentMethod() throws JsonProcessingException {
        testDataHelper.prepareDataWithMerchantIdReturn("USD");
        var payOutRequestDto = testDataHelper.createPayOutRequestDto("USD", "TEST", BigDecimal.valueOf(1000), "11/25");

        final String jsonBody = objectMapper.writeValueAsString(payOutRequestDto);

        var result = webTestClient.post().uri(PAYOUT_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(jsonBody)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(String.class);

        var content = result.returnResult().getResponseBody();
        assertNotNull(content);
        assertTrue(content.contains("PAYMENT_METHOD_NOT_ALLOWED"));
    }

    @Test
    @WithMockUser(username = "testClientId")
    void test400BadRequestNotValidAmount() throws JsonProcessingException {
        testDataHelper.prepareDataWithMerchantIdReturn("USD");
        var payOutRequestDto = testDataHelper.createPayOutRequestDto("USD", "CARD", BigDecimal.ZERO, "11/25");

        final String jsonBody = objectMapper.writeValueAsString(payOutRequestDto);

        var result = webTestClient.post().uri(PAYOUT_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(jsonBody)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(String.class);

        var content = result.returnResult().getResponseBody();
        assertNotNull(content);
        assertTrue(content.contains("PAYOUT_MIN_AMOUNT"));
    }


    @Test
    @WithMockUser(username = "testClientId")
    void test200OKShouldReturnAllPayOuts() {
        var merchantId = testDataHelper.prepareDataWithMerchantIdReturn("USD");
        testDataHelper.saveTransaction(testDataHelper.createTransaction(merchantId, "payout", "IN_PROGRESS", "OK", null)).block();
        testDataHelper.saveTransaction(testDataHelper.createTransaction(merchantId, "payout", "COMPLETED", "OK", null)).block();
        testDataHelper.saveTransaction(testDataHelper.createTransaction(merchantId, "payout", "FAILED", "PAYMENT_IS_UNSUCCESSFULLY_NO_WALLET_FOR_CURRENCY_FOUND", null)).block();

        final UriComponentsBuilder uriBuilder = fromUriString(PAYOUT_PATH + "/list");

        webTestClient.get()
            .uri(uriBuilder.build().encode().toUri())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody(ApiResponseDto.class)
            .consumeWith(response -> {
                var apiResponseDto = response.getResponseBody();
                assert apiResponseDto != null;
                List<PayoutResponseDto> payouts = apiResponseDto.getTransactionList();
                assertThat(payouts).hasSize(3);
            });
    }

    @Test
    @WithMockUser(username = "testClientId")
    void test200OKShouldReturnOnePayoutByDataFilter() {
        var merchantId = testDataHelper.prepareDataWithMerchantIdReturn("USD");
        testDataHelper.saveTransaction(testDataHelper.createTransaction(merchantId, "payout", "IN_PROGRESS", "OK", LocalDateTime.now().minusDays(5))).block();
        testDataHelper.saveTransaction(testDataHelper.createTransaction(merchantId, "payout", "COMPLETED", "OK", LocalDateTime.now().minusDays(7))).block();
        testDataHelper.saveTransaction(testDataHelper.createTransaction(merchantId, "payout", "FAILED", "PAYMENT_IS_UNSUCCESSFULLY_NO_WALLET_FOR_CURRENCY_FOUND", LocalDateTime.now().minusDays(2))).block();

        final UriComponentsBuilder uriBuilder = fromUriString(PAYOUT_PATH + "/list")
            .queryParam("startDate", LocalDateTime.now().minusDays(6));

        webTestClient.get()
            .uri(uriBuilder.build().encode().toUri())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody(ApiResponseDto.class)
            .consumeWith(response -> {
                var apiResponseDto = response.getResponseBody();
                assert apiResponseDto != null;
                var payouts = apiResponseDto.getTransactionList();
                assertThat(payouts).hasSize(2);
            });
    }

    @Test
    @WithMockUser(username = "testClientId")
    void test200OKShouldReturnPayoutById() throws JsonProcessingException {
        var merchantId = testDataHelper.prepareDataWithMerchantIdReturn("USD");
        var savedTransactionOne = testDataHelper.saveTransaction(testDataHelper.createTransaction(merchantId, "payout", "IN_PROGRESS", "OK", null)).block();
        var savedTransactionTwo = testDataHelper.saveTransaction(testDataHelper.createTransaction(merchantId, "payout", "COMPLETED", "OK", null)).block();
        var savedTransactionThree = testDataHelper.saveTransaction(testDataHelper.createTransaction(merchantId, "payout", "FAILED", "TRANSACTION_IS_UNSUCCESSFULLY_NO_WALLET_FOR_CURRENCY_FOUND", null)).block();

        final UriComponentsBuilder uriBuilder = fromUriString(PAYOUT_PATH + "/" + savedTransactionOne.getId() + "/details");

        webTestClient.get()
            .uri(uriBuilder.build().encode().toUri())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody(PayoutResponseDto.class)
            .consumeWith(response -> {
                var payout = response.getResponseBody();
                assert payout != null;
                assertEquals(payout.payoutId(), savedTransactionOne.getId());
                assertThat(payout.payoutId()).isNotEqualTo(savedTransactionTwo.getId());
                assertThat(payout.payoutId()).isNotEqualTo(savedTransactionThree.getId());
            });
    }
}