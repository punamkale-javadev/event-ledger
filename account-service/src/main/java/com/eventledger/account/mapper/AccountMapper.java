package com.eventledger.account.mapper;

import com.eventledger.account.dto.response.AccountResponse;
import com.eventledger.account.dto.response.TransactionResponse;
import com.eventledger.account.entity.AccountEntity;
import com.eventledger.account.entity.AccountTransactionEntity;

import java.util.List;

public final class AccountMapper {

    private AccountMapper() {
    }

    public static TransactionResponse toTransactionResponse(
            AccountTransactionEntity entity) {

        return new TransactionResponse(
                entity.getEventId(),
                entity.getType().name(),
                entity.getAmount(),
                entity.getEventTimestamp()
        );
    }

    public static AccountResponse toAccountResponse(
            AccountEntity account,
            List<TransactionResponse> transactions) {

        return new AccountResponse(
                account.getAccountId(),
                account.getBalance(),
                transactions
        );
    }
}