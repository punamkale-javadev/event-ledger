package com.eventledger.gateway.exception;

public class DuplicateEventException
        extends RuntimeException {

    public DuplicateEventException(String eventId) {

        super("Duplicate event detected : " + eventId);
    }
}