package com.eventledger.gateway.dto.response;
import java.util.List;

public record EventListResponse(

        String accountId,

        List<EventResponse> events

) {
}
