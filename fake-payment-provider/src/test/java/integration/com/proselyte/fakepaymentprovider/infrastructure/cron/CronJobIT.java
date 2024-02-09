package integration.com.proselyte.fakepaymentprovider.infrastructure.cron;

import com.proselyte.fakepaymentprovider.FakePaymentProviderApplication;
import com.proselyte.fakepaymentprovider.infrastructure.cron.CronJob;
import integration.com.proselyte.fakepaymentprovider.infrastructure.api.ApiBaseTest;
import integration.com.proselyte.fakepaymentprovider.infrastructure.util.TestDataHelper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.testcontainers.shaded.org.awaitility.Durations;

import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest(classes = {
    FakePaymentProviderApplication.class,
    TestConfiguration.class,
    TestDataHelper.class
}, webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    properties = "scheduling.enabled=true")
class CronJobIT extends ApiBaseTest {

    @SpyBean
    private CronJob cronJob;

    @Test
    void proceedTransactionsTest() {
        await()
            .atMost(Durations.TEN_SECONDS)
            .untilAsserted(() -> verify(cronJob, atMostOnce()).proceedTransactions());
    }

    @Test
    void proceedPayoutsTest() {
        await()
            .atMost(Durations.TEN_SECONDS)
            .untilAsserted(() -> verify(cronJob, atMost(1)).proceedPayouts());
    }
}