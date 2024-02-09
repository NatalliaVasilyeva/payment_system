package com.proselyte.fakepaymentprovider.infrastructure.cron;


import com.proselyte.fakepaymentprovider.domain.service.WebhookExecutionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Slf4j
@Component
public class CronJob {

    private final WebhookExecutionService webhookExecutionService;

    @Async()
    @Scheduled(cron = "*/10 * * * * *")
    public void proceedTransactions() {
        log.info("Update start");
        webhookExecutionService.executePaymentWebhook("transaction")
            .subscribe(response -> log.info("Update finish"));
    }

    @Async()
    @Scheduled(cron = "*/10 * * * * *")
    public void proceedPayouts() {
        log.info("Update start");
        webhookExecutionService.executePaymentWebhook("payout")
            .subscribe(response -> log.info("Update finish"));
    }
}