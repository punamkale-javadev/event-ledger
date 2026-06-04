package com.eventledger.common.dto;

import java.time.Instant;

public record ErrorResponse(

        Instant timestamp,

        Integer status,

        String error,

        String message,

        String traceId

) {
}
