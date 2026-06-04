package com.eventledger.account.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponse(

        String eventId,

        String type,

        BigDecimal amount,

        Instant eventTimestamp

) {
}