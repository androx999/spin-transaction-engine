package io.javabrains.transaccionesspinprueba.infrastructure.persistence.repository;

import io.javabrains.transaccionesspinprueba.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


/**
 * Spring Data JPA repository para TransactionEntity.
 *
 * Provee Operaciones CRUD incluyendo the filtering capabilities required by
 * the GET /transactions endpoint (accountId, status, type) with pagination.
 */
public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, UUID> {
}
