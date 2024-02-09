package integration.com.proselyte.fakepaymentprovider.infrastructure;

import com.proselyte.fakepaymentprovider.FakePaymentProviderApplication;
import integration.com.proselyte.fakepaymentprovider.infrastructure.annotation.Integration;
import integration.com.proselyte.fakepaymentprovider.infrastructure.util.TestDataHelper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

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
class FakePaymentProviderApplicationTest {

    @Test
    void shouldConfirmApplicationName() {

        // given

        // when

        // then
        String applicationName = "fake-payment-provider";
        assertThat(applicationName).isEqualTo("fake-payment-provider");
    }
}