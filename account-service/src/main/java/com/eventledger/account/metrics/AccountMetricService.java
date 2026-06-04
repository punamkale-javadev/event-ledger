package com.eventledger.account.metrics;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class AccountMetricService {
    private final Counter
            transactionCounter;

    public AccountMetricService(
            MeterRegistry registry
    ) {

        transactionCounter =
                Counter
                        .builder(
                                "account.transactions"
                        )
                        .description(
                                "Processed transactions"
                        )
                        .register(
                                registry
                        );
    }

    public void increment() {

        transactionCounter
                .increment();

    }

}

