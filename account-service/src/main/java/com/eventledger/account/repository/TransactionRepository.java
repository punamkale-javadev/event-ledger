package com.eventledger.account.repository;

import com.eventledger.account.entity.AccountTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository
        extends JpaRepository<AccountTransactionEntity, Long> {

    boolean existsByEventId(String eventId);

    List<AccountTransactionEntity>
    findTop10ByAccountIdOrderByEventTimestampDesc(
            String accountId);

    List<AccountTransactionEntity>
    findByAccountIdOrderByEventTimestampAsc(
            String accountId);
}