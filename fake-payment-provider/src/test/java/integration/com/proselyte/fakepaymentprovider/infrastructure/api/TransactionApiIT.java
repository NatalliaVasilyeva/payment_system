package integration.com.proselyte.fakepaymentprovider.infrastructure.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.proselyte.fakepaymentprovider.domain.dto.ApiResponseDto;
import com.proselyte.fakepaymentprovider.domain.dto.TopUpShotResponseDto;
import com.proselyte.fakepaymentprovider.domain.dto.TransactionResponseDto;
import com.proselyte.fakepaymentprovider.domain.model.PaymentMessage;
import com.proselyte.fakepaymentprovider.domain.model.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.proselyte.fakepaymentprovider.infrastructure.api.TransactionApi.TRANSACTION_URL_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

class TransactionApiIT extends ApiBaseTest {

    private static final String TRANSACTION_PATH = "/api/v1/" + TRANSACTION_URL_NAME;

    @Test
    @WithMockUser(username = "testClientId")
    void test201CreatedShouldCreateTransaction() throws JsonProcessingException {
        testDataHelper.prepareDataWithMerchantIdReturn("USD");
        var topUpRequestDto = testDataHelper.createTopUpRequestDto("USD", "CARD", BigDecimal.valueOf(1000), "11/25");

        final String jsonBody = objectMapper.writeValueAsString(topUpRequestDto);

        webTestClient.post().uri(TRANSACTION_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(jsonBody)
            .exchange()
            .expectStatus().isCreated()
            .expectBodyList(TopUpShotResponseDto.class)
            .hasSize(1)
            .consumeWith(response -> {
                List<TopUpShotResponseDto> transactions = response.getResponseBody();
                assert transactions != null;
                transactions.forEach(tr -> {
                    assertNotNull(tr.transactionId());
                    assertEquals(PaymentMessage.OK, tr.message());
                    assertEquals(PaymentStatus.IN_PROGRESS, tr.status());
                });
            });
    }

    @Test
    @WithMockUser(username = "testClientId")
    void test400BadRequestNotValidPaymentMethod() throws JsonProcessingException {
        testDataHelper.prepareDataWithMerchantIdReturn("USD");
        var topUpRequestDto = testDataHelper.createTopUpRequestDto("USD", "TEST", BigDecimal.valueOf(1000), "11/25");

        final String jsonBody = objectMapper.writeValueAsString(topUpRequestDto);

        var result = webTestClient.post().uri(TRANSACTION_PATH)
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
        var topUpRequestDto = testDataHelper.createTopUpRequestDto("USD", "CARD", BigDecimal.ZERO, "11/25");

        final String jsonBody = objectMapper.writeValueAsString(topUpRequestDto);

        var result = webTestClient.post().uri(TRANSACTION_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(jsonBody)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(String.class);

        var content = result.returnResult().getResponseBody();
        assertNotNull(content);
        assertTrue(content.contains("TRANSACTION_MIN_AMOUNT"));
    }

    @Test
    @WithMockUser(username = "testClientId")
    void test400BadRequestExpiredCard() throws JsonProcessingException {
        testDataHelper.prepareDataWithMerchantIdReturn("USD");
        var topUpRequestDto = testDataHelper.createTopUpRequestDto("USD", "CARD", BigDecimal.valueOf(1000), "11/20");

        final String jsonBody = objectMapper.writeValueAsString(topUpRequestDto);

        var result = webTestClient.post().uri(TRANSACTION_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(jsonBody)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(String.class);

        var content = result.returnResult().getResponseBody();
        assertNotNull(content);
        assertTrue(content.contains("Card was expired"));
    }

    @Test
    @WithMockUser(username = "testClientId")
    void test400BadRequestCardCcvNotPresent() throws JsonProcessingException {
        testDataHelper.prepareDataWithMerchantIdReturn("USD");
        var topUpRequestDto = testDataHelper.createTopUpRequestDto("USD", "CARD", BigDecimal.valueOf(1000), null);

        final String jsonBody = objectMapper.writeValueAsString(topUpRequestDto);

        var result = webTestClient.post().uri(TRANSACTION_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(jsonBody)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(String.class);

        var content = result.returnResult().getResponseBody();
        assertNotNull(content);
        assertTrue(content.contains("CVV must present"));
    }

    @Test
    @WithMockUser(username = "testClientId")
    void test400BadRequestWrongExpiredDateCardFormat() throws JsonProcessingException {
        testDataHelper.prepareDataWithMerchantIdReturn("USD");
        var topUpRequestDto = testDataHelper.createTopUpRequestDto("USD", "CARD", BigDecimal.valueOf(1000), "111/2020");

        final String jsonBody = objectMapper.writeValueAsString(topUpRequestDto);

        var result = webTestClient.post().uri(TRANSACTION_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(jsonBody)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(String.class);

        var content = result.returnResult().getResponseBody();
        assertNotNull(content);
        assertTrue(content.contains("Card expired date has wrong format"));
    }

    @Test
    @WithMockUser(username = "testClientId")
    void test200OKShouldReturnAllTransactions() throws JsonProcessingException {
        var merchantId = testDataHelper.prepareDataWithMerchantIdReturn("USD");
        testDataHelper.saveTransaction(testDataHelper.createTransaction(merchantId, "transaction", "IN_PROGRESS", "OK", null)).block();
        testDataHelper.saveTransaction(testDataHelper.createTransaction(merchantId, "transaction", "APPROVED", "OK", null)).block();
        testDataHelper.saveTransaction(testDataHelper.createTransaction(merchantId, "transaction", "FAILED", "TRANSACTION_IS_UNSUCCESSFULLY_NO_WALLET_FOR_CURRENCY_FOUND", null)).block();

        final UriComponentsBuilder uriBuilder = fromUriString(TRANSACTION_PATH + "/list");

        webTestClient.get()
            .uri(uriBuilder.build().encode().toUri())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody(ApiResponseDto.class)
            .consumeWith(response -> {
                var apiResponseDto = response.getResponseBody();
                assert apiResponseDto != null;
                List<TransactionResponseDto> transactions = apiResponseDto.getTransactionList();
                assertThat(transactions).hasSize(3);
            });
    }

    @Test
    @WithMockUser(username = "testClientId")
    void test200OKShouldReturnOneTransactionByDataFilter() {
        var merchantId = testDataHelper.prepareDataWithMerchantIdReturn("USD");
        testDataHelper.saveTransaction(testDataHelper.createTransaction(merchantId, "transaction", "IN_PROGRESS", "OK", LocalDateTime.now().minusDays(5))).block();
        testDataHelper.saveTransaction(testDataHelper.createTransaction(merchantId, "transaction", "APPROVED", "OK", LocalDateTime.now().minusDays(7))).block();
        testDataHelper.saveTransaction(testDataHelper.createTransaction(merchantId, "transaction", "FAILED", "TRANSACTION_IS_UNSUCCESSFULLY_NO_WALLET_FOR_CURRENCY_FOUND", LocalDateTime.now().minusDays(2))).block();

        final UriComponentsBuilder uriBuilder = fromUriString(TRANSACTION_PATH + "/list")
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
                var transactions = apiResponseDto.getTransactionList();
                assertThat(transactions).hasSize(2);
            });
    }

    @Test
    @WithMockUser(username = "testClientId")
    void test200OKShouldReturnTransactionById() throws JsonProcessingException {
        var merchantId = testDataHelper.prepareDataWithMerchantIdReturn("USD");
        var savedTransactionOne = testDataHelper.saveTransaction(testDataHelper.createTransaction(merchantId, "transaction", "IN_PROGRESS", "OK", null)).block();
        var savedTransactionTwo = testDataHelper.saveTransaction(testDataHelper.createTransaction(merchantId, "transaction", "APPROVED", "OK", null)).block();
        var savedTransactionThree = testDataHelper.saveTransaction(testDataHelper.createTransaction(merchantId, "transaction", "FAILED", "TRANSACTION_IS_UNSUCCESSFULLY_NO_WALLET_FOR_CURRENCY_FOUND", null)).block();

        final UriComponentsBuilder uriBuilder = fromUriString(TRANSACTION_PATH + "/" + savedTransactionOne.getId() + "/details");

        webTestClient.get()
            .uri(uriBuilder.build().encode().toUri())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody(TransactionResponseDto.class)
            .consumeWith(response -> {
                var transaction = response.getResponseBody();
                assert transaction != null;
                assertEquals(transaction.transactionId(), savedTransactionOne.getId());
                assertThat(transaction.transactionId()).isNotEqualTo(savedTransactionTwo.getId());
                assertThat(transaction.transactionId()).isNotEqualTo(savedTransactionThree.getId());
            });
    }

}