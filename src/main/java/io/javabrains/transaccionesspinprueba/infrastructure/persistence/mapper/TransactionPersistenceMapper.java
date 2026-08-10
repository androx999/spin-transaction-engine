package io.javabrains.transaccionesspinprueba.infrastructure.persistence.mapper;

import io.javabrains.transaccionesspinprueba.domain.model.Transaction;
import io.javabrains.transaccionesspinprueba.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.stereotype.Component;

@Component
public class TransactionPersistenceMapper {
    public TransactionEntity toEntity(Transaction transaction) {
        return new TransactionEntity(
                transaction.getId(),
                transaction.getAccountId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getDescription(),
                transaction.getStatus(),
                transaction.getProviderTransactionId(),
                transaction.getBalanceAfter(),
                transaction.getErrorMessage(),
                transaction.getCreatedAt()
        );
    }

    public Transaction toDomain(TransactionEntity entity) {
        return new Transaction(
                        entity.getId(),
                        entity.getAccountId(),
                        entity.getType(),
                        entity.getAmount(),
                        entity.getCurrency(),
                        entity.getDescription(),
                        entity.getStatus(),
                        entity.getProviderTransactionId(),
                        entity.getBalanceAfter(),
                entity.getCreatedAt(),
                entity.getErrorMessage()
                );
    }
}
