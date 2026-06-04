package com.eventledger.gateway.metrics;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import org.springframework.stereotype.Service;
@Service
public class EventMetricsService {

    private final Counter
            submittedEvents;

    public EventMetricsService(
            MeterRegistry registry
    ) {

        submittedEvents =
                Counter
                        .builder(
                                "events.submitted"
                        )
                        .description(
                                "Number of events submitted"
                        )
                        .register(
                                registry
                        );
    }

    public void increment() {

        submittedEvents.increment();

    }
}
