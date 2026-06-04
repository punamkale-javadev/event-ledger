package com.eventledger.gateway.exception;

public class AccountServiceUnavailableException
        extends RuntimeException {

    public AccountServiceUnavailableException(String msg) {

        super(msg);
    }
}