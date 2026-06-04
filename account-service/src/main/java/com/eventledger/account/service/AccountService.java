package com.eventledger.account.service;

import com.eventledger.account.dto.response.AccountResponse;
import com.eventledger.account.dto.response.BalanceResponse;
import com.eventledger.account.dto.response.TransactionResponse;
import com.eventledger.account.entity.AccountEntity;
import com.eventledger.account.entity.AccountTransactionEntity;
import com.eventledger.account.exception.AccountNotFoundException;
import com.eventledger.account.mapper.AccountMapper;
import com.eventledger.account.metrics.AccountMetricService;
import com.eventledger.account.repository.AccountRepository;
import com.eventledger.account.repository.TransactionRepository;
import com.eventledger.common.dto.TransactionRequest;
import com.eventledger.common.enums.EventType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {
    private static final Logger log =
            LoggerFactory.getLogger( AccountService.class );

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AccountMetricService metricService;

    public void applyTransaction(
            TransactionRequest request) {
        log.info(
                "Applying transaction for account={}",

                request.accountId()
        );
        /*
         * Idempotency
         */
        if (transactionRepository.existsByEventId(
                request.eventId())) {
            return;
        }

        AccountTransactionEntity transaction =
                AccountTransactionEntity.builder()
                        .eventId(request.eventId())
                        .accountId(request.accountId())
                        .type(request.type())
                        .amount(request.amount())
                        .eventTimestamp(
                                request.eventTimestamp())
                        .createdAt(Instant.now())
                        .build();
        log.info(
                "Balance updated"
        );

        transactionRepository.save(transaction);
        metricService.increment();
        recalculateBalance(request.accountId());
    }

    private void recalculateBalance(
            String accountId) {

        List<AccountTransactionEntity> transactions =
                transactionRepository
                        .findByAccountIdOrderByEventTimestampAsc(
                                accountId);

        BigDecimal balance = BigDecimal.ZERO;

        for (AccountTransactionEntity tx : transactions) {

            if (tx.getType() == EventType.CREDIT) {

                balance = balance.add(
                        tx.getAmount());

            } else {

                balance = balance.subtract(
                        tx.getAmount());
            }
        }

        AccountEntity account =
                accountRepository
                        .findById(accountId)
                        .orElse(
                                AccountEntity.builder()
                                        .accountId(accountId)
                                        .build()
                        );

        account.setBalance(balance);

        accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public BalanceResponse getBalance(
            String accountId) {

        AccountEntity account =
                accountRepository.findById(accountId)
                        .orElseThrow(() ->
                                new AccountNotFoundException(
                                        accountId));

        return new BalanceResponse(
                accountId,
                account.getBalance());
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccount(
            String accountId) {

        AccountEntity account =
                accountRepository.findById(accountId)
                        .orElseThrow(() ->
                                new AccountNotFoundException(
                                        accountId));

        List<TransactionResponse> transactions =
                transactionRepository
                        .findTop10ByAccountIdOrderByEventTimestampDesc(
                                accountId)
                        .stream()
                        .map(AccountMapper::toTransactionResponse)
                        .toList();

        return AccountMapper.toAccountResponse(
                account,
                transactions);
    }
}