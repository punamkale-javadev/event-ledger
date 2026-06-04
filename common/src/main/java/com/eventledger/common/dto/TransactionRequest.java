package com.eventledger.common.dto;

import com.eventledger.common.enums.EventType;

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