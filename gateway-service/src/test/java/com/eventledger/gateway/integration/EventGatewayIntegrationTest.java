package com.eventledger.gateway.integration;

import com.eventledger.gateway.GatewayServiceApplication;
import com.eventledger.gateway.client.AccountServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.web.client.TestRestTemplate;

import org.springframework.boot.test.web.server.LocalServerPort;

import org.springframework.http.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@SpringBootTest(classes =
                        GatewayServiceApplication.class,
        webEnvironment =
                SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = { "ACCOUNT_SERVICE_URL=http://localhost:8081"}
)
class EventGatewayIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;
    @MockitoBean
    private AccountServiceClient accountClient;
    @BeforeEach
    void setup(){
        doNothing() .when(accountClient) .applyTransaction( any(),any());
    }

    @Test
    void shouldCreateEvent() {

        String payload = """
    {
      "eventId":"evt-001",
      "accountId":"acct-001",
      "type":"CREDIT",
      "amount":200,
      "currency":"USD",
      "eventTimestamp":
      "2026-05-15T14:02:11Z"
    }
    """;

        HttpHeaders headers =
                new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_JSON);

        ResponseEntity<String>
                response =
                restTemplate.postForEntity(
                        "/events",
                        new HttpEntity<>(
                                payload,
                                headers),
                        String.class);

        assertThat(
                response
                        .getStatusCode())
                .isEqualTo(
                        HttpStatus.CREATED);
    }

    @Test
    void duplicateEventShouldReturnOriginalEvent() {
        String payload = """
{
  "eventId":"evt-002",
  "accountId":"acct-001",
  "type":"CREDIT",
  "amount":100,
  "currency":"USD",
  "eventTimestamp":
  "2026-05-15T14:02:11Z"
}
""";

        HttpHeaders headers =
                new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_JSON);

        HttpEntity<String>
                request =
                new HttpEntity<>(
                        payload,
                        headers);

        ResponseEntity<String>
                first =
                restTemplate.postForEntity(
                        "/events",
                        request,
                        String.class);

        assertThat(
                first.getStatusCode())
                .isEqualTo(
                        HttpStatus.CREATED);

        ResponseEntity<String>
                duplicate =
                restTemplate.postForEntity(
                        "/events",
                        request,
                        String.class);

        assertThat(
                duplicate.getStatusCode())
                .isEqualTo(
                        HttpStatus.OK);

        assertThat(
                duplicate.getBody())
                .contains(
                        "evt-002");

    }
    @Test
    void healthShouldReturnUp() {

        ResponseEntity<String>
                response =
                restTemplate.getForEntity(
                        "/health",
                        String.class);

        assertThat(
                response
                        .getBody())
                .contains(
                        "UP");
    }

}
