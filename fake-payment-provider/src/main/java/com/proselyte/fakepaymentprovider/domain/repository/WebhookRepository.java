package com.proselyte.fakepaymentprovider.domain.repository;

import com.proselyte.fakepaymentprovider.domain.entity.Webhook;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WebhookRepository extends R2dbcRepository<Webhook, UUID> {

}