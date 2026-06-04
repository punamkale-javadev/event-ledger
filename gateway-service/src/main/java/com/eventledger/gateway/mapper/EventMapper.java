package com.eventledger.gateway.mapper;

import com.eventledger.gateway.dto.request.EventRequest;
import com.eventledger.gateway.dto.response.EventResponse;
import com.eventledger.gateway.entity.EventEntity;

import java.time.Instant;
import java.util.Collections;

public final class EventMapper {

    private EventMapper() {
    }

    public static EventEntity toEntity(
            EventRequest request) {

        return EventEntity.builder()
                .eventId(request.eventId())
                .accountId(request.accountId())
                .type(request.type())
                .amount(request.amount())
                .currency(request.currency())
                .eventTimestamp(request.eventTimestamp())
                .metadata(
                        request.metadata() == null
                                ? null
                                : request.metadata().toString())
                .createdAt(Instant.now())
                .build();
    }

    public static EventResponse toResponse(
            EventEntity entity) {

        return new EventResponse(
                entity.getEventId(),
                entity.getAccountId(),
                entity.getType().name(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getEventTimestamp(),
                Collections.emptyMap()
        );
    }
}