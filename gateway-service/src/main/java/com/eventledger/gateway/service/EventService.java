package com.eventledger.gateway.service;

import com.eventledger.common.dto.TransactionRequest;
import com.eventledger.gateway.client.AccountServiceClient;
import com.eventledger.gateway.dto.request.EventRequest;
import com.eventledger.gateway.dto.response.EventListResponse;
import com.eventledger.gateway.dto.response.EventResponse;
import com.eventledger.gateway.dto.response.EventSubmitResult;
import com.eventledger.gateway.entity.EventEntity;
import com.eventledger.gateway.exception.AccountServiceUnavailableException;
import com.eventledger.gateway.exception.EventNotFoundException;
import com.eventledger.gateway.mapper.EventMapper;
import com.eventledger.gateway.metrics.EventMetricsService;
import com.eventledger.gateway.repository.EventRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {
    private static final Logger log =
            LoggerFactory.getLogger( EventService.class);

    private final EventRepository eventRepository;
    private final AccountServiceClient accountServiceClient;
    private final EventMetricsService metricService;

    @CircuitBreaker(name = "accountService", fallbackMethod = "accountServiceFallback")
    public EventSubmitResult submitEvent(EventRequest request) {

        EventEntity existing = eventRepository.findById
                (request.eventId()).orElse(null);

        if (existing != null) {

            return new EventSubmitResult(EventMapper.toResponse(existing), true);
        }

        TransactionRequest transaction = new TransactionRequest(
                request.eventId(),
                request.accountId(),
                request.type(),
                request.amount(),
                request.eventTimestamp());
        log.info(
                "Calling Account Service for accountId={}",

                request.accountId()
        );

        accountServiceClient.applyTransaction(MDC.get( "traceId" ),transaction);

        EventEntity saved = eventRepository.save(EventMapper.toEntity(request));
        metricService.increment();
        return new EventSubmitResult(EventMapper.toResponse(saved), false);

    }

    @Transactional(readOnly = true)
    public EventResponse getEvent(String eventId) {

        EventEntity entity = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));

        return EventMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public EventListResponse getEventsByAccount(String accountId) {

        List<EventResponse> responses = eventRepository.findByAccountIdOrderByEventTimestampAsc(accountId).stream().map(EventMapper::toResponse).toList();

        return new EventListResponse(accountId, responses);
    }

    private EventSubmitResult accountServiceFallback(
            EventRequest request,
            Exception ex
    ) {
        throw new AccountServiceUnavailableException(
                "Account Service is currently unavailable");
    }

}