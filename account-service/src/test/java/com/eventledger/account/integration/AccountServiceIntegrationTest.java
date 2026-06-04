package com.eventledger.account.integration;

import com.eventledger.account.AccountServiceApplication;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.web.client.TestRestTemplate;

import org.springframework.boot.test.web.server.LocalServerPort;

import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes =
                AccountServiceApplication.class,
webEnvironment =
SpringBootTest
        .WebEnvironment
        .RANDOM_PORT
        )
class AccountServiceIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void shouldApplyTransaction() {
        String accountId =
                "acc-100";
        String payload = """
                {
                    "eventId":"evt-100",
                    "accountId":"acc-100",
                    "type":"CREDIT",
                    "amount":100,
                    "eventTimestamp":
                    "2026-06-04T10:00:00Z"
                }
                """;

        HttpHeaders headers =
                new HttpHeaders();

        headers.set(
                "traceId",
                "test-trace"
        );

        headers.setContentType(
                MediaType.APPLICATION_JSON
        );

        ResponseEntity<String>
                response =
                restTemplate.postForEntity(

                        "http://localhost:"
                                + port
                                + "/internal/transactions",

                        new HttpEntity<>(
                                payload,
                                headers
                        ),

                        String.class
                );

        assertThat(
                response
                        .getStatusCode()
        ).isEqualTo(
                HttpStatus.OK
        );
        ResponseEntity<String>
                balanceResponse =

                restTemplate.getForEntity(

                        "http://localhost:"
                                + port
                                + "/accounts/"
                                + accountId
                                + "/balance",

                        String.class
                );

        assertThat(
                balanceResponse
                        .getStatusCode()
        )
                .isEqualTo(
                        HttpStatus.OK
                );

        assertThat(
                balanceResponse
                        .getBody()
        )
                .contains(
                        "100"
                );
    }

    @Test
    void healthShouldReturnUp() {

        ResponseEntity<String>
                response =
                restTemplate.getForEntity(

                        "http://localhost:"
                                + port
                                + "/health",

                        String.class
                );

        assertThat(
                response
                        .getStatusCode()
        ).isEqualTo(
                HttpStatus.OK
        );

        assertThat(
                response
                        .getBody()
        ).contains(
                "UP"
        );
    }
}
