package com.eventledger.account.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record AccountResponse(

        String accountId,

        BigDecimal balance,

        List<TransactionResponse> recentTransactions

) {
}