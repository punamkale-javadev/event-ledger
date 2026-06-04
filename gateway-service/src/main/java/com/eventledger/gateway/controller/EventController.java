package com.eventledger.gateway.controller;

import com.eventledger.common.dto.ErrorResponse;
import com.eventledger.gateway.dto.request.EventRequest;
import com.eventledger.gateway.dto.response.EventListResponse;
import com.eventledger.gateway.dto.response.EventResponse;
import com.eventledger.gateway.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse submitTransaction(@Valid @RequestBody EventRequest eventRequest){
            return eventService.submitEvent(eventRequest);
    }

    @GetMapping
    public EventListResponse getEventsByAccount(@RequestParam("account") String accountId){
            return eventService.getEventsByAccount(accountId);
    }

    /**
     * Get Event By Id
     */
    @GetMapping("/{eventId}")
    public EventResponse getEvent(
            @PathVariable String eventId) {

        return eventService.getEvent(eventId);
    }

}
