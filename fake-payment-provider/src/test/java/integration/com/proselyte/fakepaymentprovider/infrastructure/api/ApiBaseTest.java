package integration.com.proselyte.fakepaymentprovider.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proselyte.fakepaymentprovider.FakePaymentProviderApplication;
import integration.com.proselyte.fakepaymentprovider.infrastructure.annotation.Integration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.reactive.server.WebTestClient;
import integration.com.proselyte.fakepaymentprovider.infrastructure.util.TestDataHelper;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;


@Integration
@SpringJUnitWebConfig
@SpringBootTest(classes = {
    FakePaymentProviderApplication.class,
    TestConfiguration.class,
    TestDataHelper.class
}, webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    properties = "scheduling.enabled=false")
@TestPropertySource(locations = "classpath:/application-test.yaml")
@Testcontainers
@ActiveProfiles("test")
public abstract class ApiBaseTest {

    protected WebTestClient webTestClient;

    @Autowired
    protected ApplicationContext context;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected TestDataHelper testDataHelper;

    protected HttpHeaders commonHeaders;

    @BeforeEach
    void setUp() {
        this.webTestClient = WebTestClient
            .bindToApplicationContext(this.context)
            // add Spring Security test Support
            .apply(springSecurity())
            .configureClient()
            .filter(basicAuthentication("testClientId", "testClientSecret"))
            .defaultHeader("Content-type", String.valueOf(MediaType.APPLICATION_JSON))
            .build();
    }

    @AfterEach
    void tearDown() {
        testDataHelper.deleteWallet()
            .then(testDataHelper.deleteTransactions())
            .then(testDataHelper.deleteMerchant())
            .block();
    }

    private static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:latest")
        .withDatabaseName("fake-payment-provider-test")
        .withUsername("fake-payment-provider-test")
        .withPassword("fake-payment-provider-test")
        .withInitScript("init.sql");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.flyway.url", container::getJdbcUrl);
        registry.add("spring.flyway.user", container::getUsername);
        registry.add("spring.flyway.password", container::getPassword);

        registry.add("spring.r2dbc.url", () -> container.getJdbcUrl().replace("jdbc:", "r2dbc:"));
        registry.add("spring.r2dbc.username", container::getUsername);
        registry.add("spring.r2dbc.password", container::getPassword);
    }

    @BeforeAll
    static void beforeAll() {
        container.setWaitStrategy(
            new LogMessageWaitStrategy()
                .withRegEx(".*database system is ready to accept connections.*\\s")
                .withTimes(1)
                .withStartupTimeout(Duration.of(60, ChronoUnit.SECONDS))
        );
        container.start();
    }

    @AfterAll
    static void afterAll() {
    }

    public void setAuthorizationHeader() {
        commonHeaders = new HttpHeaders();
        commonHeaders.set("x-client-id", "test-service");
        commonHeaders.set("x-client-version", "v1.0.0");
        commonHeaders.set("Authorization", "Bearer xxxx");
    }
}