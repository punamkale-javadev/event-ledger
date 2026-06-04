package com.eventledger.gateway.service;

import com.eventledger.common.dto.TransactionRequest;
import com.eventledger.gateway.client.AccountServiceClient;
import com.eventledger.gateway.dto.request.EventRequest;
import com.eventledger.gateway.dto.response.EventListResponse;
import com.eventledger.gateway.dto.response.EventResponse;
import com.eventledger.gateway.entity.EventEntity;
import com.eventledger.gateway.exception.AccountServiceUnavailableException;
import com.eventledger.gateway.exception.EventNotFoundException;
import com.eventledger.gateway.mapper.EventMapper;
import com.eventledger.gateway.repository.EventRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final AccountServiceClient accountServiceClient;

    @CircuitBreaker(
            name = "accountService",
            fallbackMethod = "accountServiceFallback"
    )
    public EventResponse submitEvent(
            EventRequest request) {

        EventEntity existing =
                eventRepository.findById(
                                request.eventId())
                        .orElse(null);

        if (existing != null) {
            return EventMapper.toResponse(existing);
        }

        TransactionRequest transactionRequest =
                new TransactionRequest(
                        request.eventId(),
                        request.accountId(),
                        request.type(),
                        request.amount(),
                        request.eventTimestamp()
                );

        accountServiceClient
                .applyTransaction(
                        transactionRequest);

        EventEntity entity =
                eventRepository.save(
                        EventMapper.toEntity(request));

        return EventMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public EventResponse getEvent(
            String eventId) {

        EventEntity entity =
                eventRepository.findById(eventId)
                        .orElseThrow(() ->
                                new EventNotFoundException(eventId));

        return EventMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public EventListResponse getEventsByAccount(
            String accountId) {

        List<EventResponse> responses =
                eventRepository
                        .findByAccountIdOrderByEventTimestampAsc(
                                accountId)
                        .stream()
                        .map(EventMapper::toResponse)
                        .toList();

        return new EventListResponse(
                accountId,
                responses
        );
    }
    private EventResponse accountServiceFallback(
            EventRequest request,
            Exception ex) {

        throw new AccountServiceUnavailableException(
                "Account Service is currently unavailable"
        );
    }
}