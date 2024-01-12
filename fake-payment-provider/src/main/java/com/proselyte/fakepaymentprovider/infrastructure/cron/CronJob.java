package com.proselyte.fakepaymentprovider.infrastructure.cron;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class CronJob {

    private final WebClient webclient;

    private final String transactionSourceHost;

    private final String payoutSourceHost;

    @Autowired
    public CronJob(WebClient webclient,
                   @Value("${service.source.webhook.transaction.host}") String transactionSourceHost,
                       @Value("${service.source.webhook.payout.host}") String payoutSourceHost) {
        this.webclient = webclient;
        this.transactionSourceHost = transactionSourceHost;
        this.payoutSourceHost = payoutSourceHost;
    }

    @Async()
    @Scheduled(cron = "*/20 * * * * *")
    public void procceedTransactions() {

        webclient.post()
                    .uri(transactionSourceHost)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toBodilessEntity()
            .subscribe();
    }

    @Async()
    @Scheduled(cron = "*/20 * * * * *")
    public void procceedPayouts() {
        webclient.post()
            .uri(payoutSourceHost)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .toBodilessEntity()
            .subscribe();
    }
}