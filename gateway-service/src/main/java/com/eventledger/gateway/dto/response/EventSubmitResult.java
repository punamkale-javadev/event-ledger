package com.eventledger.gateway.dto.response;

public record EventSubmitResult(
        EventResponse response,
        boolean duplicate
){}