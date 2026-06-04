package com.eventledger.account.service;

import com.eventledger.account.dto.response.AccountResponse;
import com.eventledger.account.dto.response.BalanceResponse;
import com.eventledger.account.dto.response.TransactionResponse;
import com.eventledger.account.entity.AccountEntity;
import com.eventledger.account.entity.AccountTransactionEntity;
import com.eventledger.account.exception.AccountNotFoundException;
import com.eventledger.account.mapper.AccountMapper;
import com.eventledger.account.repository.AccountRepository;
import com.eventledger.account.repository.TransactionRepository;
import com.eventledger.common.dto.TransactionRequest;
import com.eventledger.common.enums.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public void applyTransaction(
            TransactionRequest request) {

        /*
         * Idempotency Layer 2
         */
        if (transactionRepository.existsByEventId(
                request.eventId())) {

            return;
        }

        AccountEntity account =
                accountRepository
                        .findById(request.accountId())
                        .orElse(
                                AccountEntity.builder()
                                        .accountId(
                                                request.accountId())
                                        .balance(BigDecimal.ZERO)
                                        .build()
                        );

        if (request.type() == EventType.CREDIT) {

            account.setBalance(
                    account.getBalance()
                            .add(request.amount()));
        } else {

            account.setBalance(
                    account.getBalance()
                            .subtract(request.amount()));
        }

        accountRepository.save(account);

        AccountTransactionEntity tx =
                AccountTransactionEntity.builder()
                        .eventId(request.eventId())
                        .accountId(request.accountId())
                        .type(request.type())
                        .amount(request.amount())
                        .eventTimestamp(
                                request.eventTimestamp())
                        .createdAt(Instant.now())
                        .build();

        transactionRepository.save(tx);
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