package io.javabrains.transaccionesspinprueba.infrastructure.persistence.repository;

import io.javabrains.transaccionesspinprueba.domain.enums.TransactionStatus;
import io.javabrains.transaccionesspinprueba.domain.enums.TransactionType;
import io.javabrains.transaccionesspinprueba.domain.repository.TransactionRepository;
import io.javabrains.transaccionesspinprueba.domain.model.Transaction;
import io.javabrains.transaccionesspinprueba.infrastructure.persistence.entity.TransactionEntity;
import io.javabrains.transaccionesspinprueba.infrastructure.persistence.mapper.TransactionPersistenceMapper;
import io.javabrains.transaccionesspinprueba.infrastructure.persistence.specification.TransactionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    @Override
    public Page<Transaction> findAll(String accountId, TransactionStatus status, TransactionType type, Pageable pageable) {
        Specification<TransactionEntity> specification =
                TransactionSpecification
                        .hasAccountId(accountId)
                        .and(TransactionSpecification.hasStatus(status))
                        .and(TransactionSpecification.hasType(type));
        return jpaRepository.findAll(specification,pageable).map(mapper::toDomain);
    }
}
