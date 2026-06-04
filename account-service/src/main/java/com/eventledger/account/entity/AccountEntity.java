package com.eventledger.account.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountEntity {

    @Id
    @Column(name = "account_id")
    private String accountId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;
}