package com.eventledger.gateway.mapper;

import com.eventledger.gateway.dto.request.EventRequest;
import com.eventledger.gateway.dto.response.EventResponse;
import com.eventledger.gateway.entity.EventEntity;

import java.time.Instant;
import java.util.Map;

public final class EventMapper {

    private EventMapper() {
    }

    public static EventEntity toEntity(
            EventRequest request,
            String metadataJson) {

        return EventEntity.builder()
                .eventId(request.eventId())
                .accountId(request.accountId())
                .type(request.type())
                .amount(request.amount())
                .currency(request.currency())
                .eventTimestamp(request.eventTimestamp())
                .metadata(metadataJson)
                .createdAt(Instant.now())
                .build();
    }

    public static EventResponse toResponse(
            EventEntity entity,
            Map<String, Object> metadata,
            String status) {

        return new EventResponse(
                entity.getEventId(),
                entity.getAccountId(),
                entity.getType().name(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getEventTimestamp(),
                metadata,
                status
        );
    }
}