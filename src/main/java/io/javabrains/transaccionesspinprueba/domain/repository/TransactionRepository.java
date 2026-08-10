package io.javabrains.transaccionesspinprueba.domain.repository;

import io.javabrains.transaccionesspinprueba.domain.model.Transaction;

import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository {
    Transaction save(Transaction transaction);

    Optional<Transaction> findById(UUID id);
}
