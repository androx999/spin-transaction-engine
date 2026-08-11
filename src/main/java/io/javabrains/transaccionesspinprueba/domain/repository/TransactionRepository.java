package io.javabrains.transaccionesspinprueba.domain.repository;

import io.javabrains.transaccionesspinprueba.domain.enums.TransactionStatus;
import io.javabrains.transaccionesspinprueba.domain.enums.TransactionType;
import io.javabrains.transaccionesspinprueba.domain.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository {
    Transaction save(Transaction transaction);

    Optional<Transaction> findById(UUID id);

    Page<Transaction> findAll(
            String accountId,
            TransactionStatus status,
            TransactionType type,
            Pageable pageable
    );
}
