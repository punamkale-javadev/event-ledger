package com.eventledger.gateway.exception;

public class AccountServiceUnavailableException
        extends RuntimeException {

    public AccountServiceUnavailableException() {

        super("Account Service is currently unavailable");
    }
}