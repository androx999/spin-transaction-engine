package io.javabrains.transaccionesspinprueba.infrastructure.persistence.repository;

import io.javabrains.transaccionesspinprueba.domain.repository.TransactionRepository;
import io.javabrains.transaccionesspinprueba.domain.model.Transaction;
import io.javabrains.transaccionesspinprueba.infrastructure.persistence.entity.TransactionEntity;
import io.javabrains.transaccionesspinprueba.infrastructure.persistence.mapper.TransactionPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TransactionRepositoryAdapter implements TransactionRepository {

    private final TransactionJpaRepository jpaRepository;
    private final TransactionPersistenceMapper mapper;

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity entity = mapper.toEntity(transaction);
        TransactionEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}
