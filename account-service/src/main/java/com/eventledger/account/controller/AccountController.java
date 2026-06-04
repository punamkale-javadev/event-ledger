package com.eventledger.account.controller;

import com.eventledger.account.dto.response.AccountResponse;
import com.eventledger.account.dto.response.BalanceResponse;
import com.eventledger.account.service.AccountService;
import com.eventledger.common.dto.TransactionRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/internal/transactions")
    public void applyTransaction(@RequestHeader(
            value = "traceId", required = false) String traceId,
                                 @RequestBody TransactionRequest request) {
        MDC.put("traceId", traceId);
        accountService.applyTransaction(request);

    }

    @GetMapping("/accounts/{accountId}/balance")
    public BalanceResponse getBalance(
            @PathVariable String accountId) {

        return accountService.getBalance(accountId);
    }

    @GetMapping("/accounts/{accountId}")
    public AccountResponse getAccount(
            @PathVariable String accountId) {

        return accountService.getAccount(accountId);
    }
}