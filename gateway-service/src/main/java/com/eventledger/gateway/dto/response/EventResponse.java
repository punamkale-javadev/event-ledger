package com.eventledger.gateway.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

public record EventResponse(

        String eventId,

        String accountId,

        String type,

        BigDecimal amount,

        String currency,

        Instant eventTimestamp,

        Map<String, Object> metadata
) {
}
