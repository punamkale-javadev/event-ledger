package com.eventledger.gateway.client;

import com.eventledger.common.dto.TransactionRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "account-service",
        url = "${account.service.url}"
)
public interface AccountServiceClient {

    @PostMapping("/internal/transactions")
    void applyTransaction(@RequestBody TransactionRequest request);
}