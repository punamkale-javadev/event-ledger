package com.eventledger.gateway.dto.response;

import java.time.Instant;

public record ErrorResponse(

        Instant timestamp,

        Integer status,

        String error,

        String message,

        String traceId

) {
}
