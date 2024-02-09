package integration.com.proselyte.fakepaymentprovider.infrastructure.util;

import com.proselyte.fakepaymentprovider.domain.dto.CardRequestDto;
import com.proselyte.fakepaymentprovider.domain.dto.CustomerRequestDto;
import com.proselyte.fakepaymentprovider.domain.dto.MerchantRequestDto;
import com.proselyte.fakepaymentprovider.domain.dto.PayOutRequestDto;
import com.proselyte.fakepaymentprovider.domain.dto.TopUpRequestDto;
import com.proselyte.fakepaymentprovider.domain.entity.Merchant;
import com.proselyte.fakepaymentprovider.domain.entity.Transaction;
import com.proselyte.fakepaymentprovider.domain.entity.Wallet;
import com.proselyte.fakepaymentprovider.domain.model.PaymentMethod;
import com.proselyte.fakepaymentprovider.domain.repository.MerchantRepository;
import com.proselyte.fakepaymentprovider.domain.repository.TransactionRepository;
import com.proselyte.fakepaymentprovider.domain.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class TestDataHelper {

    @Autowired
    protected MerchantRepository merchantRepository;

    @Autowired
    protected WalletRepository walletRepository;

    @Autowired
    protected TransactionRepository transactionRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    public Mono<Merchant> saveMerchant(Merchant merchant) {
        return merchantRepository.save(merchant);
    }

    public Mono<Wallet> saveWallet(Wallet wallet) {
        return walletRepository.save(wallet);
    }

    public Mono<Transaction> saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Flux<Transaction> saveAllTransaction(List<Transaction> transactions) {
        return transactionRepository.saveAll(transactions);
    }

    public UUID prepareDataWithMerchantIdReturn(String walletCurrency) {
        var merchant = createMerchant();
        var savedMerchant = saveMerchant(merchant).block();
        var wallet = createWallet(savedMerchant.getId(), walletCurrency);
        saveWallet(wallet).block();
        return merchant.getId();
    }

    public MerchantRequestDto createMerchantRequestDto() {
        return new MerchantRequestDto("testClientId", "testClientSecret");
    }

    public TopUpRequestDto createTopUpRequestDto(String transactionCurrency, String paymentMethod, BigDecimal amount, String expDate) {
        return new TopUpRequestDto(
            paymentMethod,
            amount,
            transactionCurrency,
            UUID.randomUUID(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            createCardRequestDto(expDate),
            "EN",
            "https://leantech.ai/webhook/transaction",
            createCustomerRequestDto()
        );
    }

    public PayOutRequestDto createPayOutRequestDto(String payOutCurrency, String paymentMethod, BigDecimal amount, String expDate) {
        return new PayOutRequestDto(
            paymentMethod,
            amount,
            payOutCurrency,
            UUID.randomUUID(),
            createCardRequestDto(expDate),
            "EN",
            "https://leantech.ai/webhook/transaction",
            createCustomerRequestDto()
        );
    }

    public CardRequestDto createCardRequestDto(String expDate) {
        return new CardRequestDto("4102778822334893", expDate, 566);
    }

    public CustomerRequestDto createCustomerRequestDto() {
        return new CustomerRequestDto("John", "Doe", "DE");
    }

    public Wallet createWallet(UUID merchantId, String currency) {
        return Wallet.builder()
            .merchantId(merchantId)
            .currency(currency)
            .balance(BigDecimal.valueOf(1000))
            .build();
    }

    public Merchant createMerchant() {
        return Merchant.builder()
            .clientId("testClientId")
            .clientSecret(passwordEncoder.encode("testClientSecret"))
            .build();
    }

    public Transaction createTransaction(UUID merchantId, String type, String status, String message, LocalDateTime createdAt) {
        return Transaction.builder()
            .merchantId(merchantId)
            .paymentMethod(PaymentMethod.CARD.name())
            .amount(BigDecimal.valueOf(1000))
            .currency("USD")
            .merchantTransactionId(UUID.randomUUID())
            .createdAt(Optional.ofNullable(createdAt).orElse(LocalDateTime.now()))
            .updatedAt(LocalDateTime.now())
            .notificationUrl("https://leantech.ai/webhook/transaction")
            .cardNumber("4102778822334893")
            .cardExpirationDate("transaction".equals(type)? "11/23" : null)
            .cardCvv("transaction".equals(type)? 555 : null)
            .language("EN")
            .customerFirstName("Harry")
            .customerLastName("Potter")
            .customerCountry("UK")
            .type(type)
            .status(status)
            .message(message)
            .build();
    }

    public Mono<Void> deleteTransactions() {
        return transactionRepository.deleteAll();
    }

    public Mono<Void> deleteMerchant() {
        return merchantRepository.deleteAll();
    }

    public Mono<Void> deleteWallet() {
        return walletRepository.deleteAll();
    }

}