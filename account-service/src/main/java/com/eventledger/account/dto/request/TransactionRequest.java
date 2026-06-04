package com.eventledger.account.dto.request;

import com.eventledger.account.enums.EventType;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionRequest(

        String eventId,

        String accountId,

        EventType type,

        BigDecimal amount,

        Instant eventTimestamp

) {
}