package com.eventledger.gateway.entity;

import com.eventledger.gateway.enums.EventType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        name = "events",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_event_id",
                        columnNames = "event_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventEntity {

    @Id
    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "account_id", nullable = false)
    private String accountId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Column(name = "event_timestamp", nullable = false)
    private Instant eventTimestamp;

    @Lob
    private String metadata;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}