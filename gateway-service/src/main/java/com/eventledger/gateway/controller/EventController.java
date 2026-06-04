package com.eventledger.gateway.controller;

import com.eventledger.common.dto.ErrorResponse;
import com.eventledger.gateway.dto.request.EventRequest;
import com.eventledger.gateway.dto.response.EventListResponse;
import com.eventledger.gateway.dto.response.EventResponse;
import com.eventledger.gateway.dto.response.EventSubmitResult;
import com.eventledger.gateway.repository.EventRepository;
import com.eventledger.gateway.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {
    private static final Logger log =
            LoggerFactory.getLogger(
                    EventController.class
            );
    private final EventService eventService;
    private final EventRepository repository;

    @PostMapping
    public ResponseEntity<EventResponse> submitTransaction(@Valid @RequestBody EventRequest request) {
        log.info(
                "Received transaction request. eventId={}",
                request.eventId()
        );

        EventSubmitResult result = eventService.submitEvent(request);

        return result.duplicate()
                ?
                ResponseEntity.ok(result.response())
                :
                ResponseEntity.status(HttpStatus.CREATED).body(result.response());
    }


    @GetMapping
    public EventListResponse getEventsByAccount(@RequestParam("account") String accountId) {
        return eventService.getEventsByAccount(accountId);
    }

    /**
     * Get Event By Id
     */
    @GetMapping("/{eventId}")
    public EventResponse getEvent(@PathVariable String eventId) {

        return eventService.getEvent(eventId);
    }

}
