package io.javabrains.transaccionesspinprueba.infrastructure.persistence;

import io.javabrains.transaccionesspinprueba.TestcontainersConfiguration;
import io.javabrains.transaccionesspinprueba.domain.enums.TransactionStatus;
import io.javabrains.transaccionesspinprueba.domain.enums.TransactionType;
import io.javabrains.transaccionesspinprueba.infrastructure.persistence.entity.TransactionEntity;
import io.javabrains.transaccionesspinprueba.infrastructure.persistence.repository.TransactionJpaRepository;
import io.javabrains.transaccionesspinprueba.infrastructure.persistence.specification.TransactionSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public class TransactionJpaRepositoryIT {

    @Autowired
    private TransactionJpaRepository repository;

    @BeforeEach
    void cleanDatabase(){
        repository.deleteAll();
    }

    @Test
    void saveAndFindTransaction()
    {
        //Save Transaction
        TransactionEntity entity = new TransactionEntity();

        entity.setAccountId("acc-123456");
        entity.setType(TransactionType.CREDIT);
        entity.setAmount(new BigDecimal("15000.00"));
        entity.setCurrency("MXN");
        entity.setDescription("Transferencia recibida");
        entity.setStatus(TransactionStatus.EXECUTED);
        entity.setProviderTransactionId("txn-789");
        entity.setBalanceAfter(new BigDecimal("0.00"));
        entity.setErrorMessage(null);
        entity.setCreatedAt(Instant.now());


        TransactionEntity saved = repository.save(entity);

        //Find Transaction by Id

        Optional<TransactionEntity> result = repository.findById(saved.getId());

        assertTrue(result.isPresent());

        TransactionEntity transaction = result.get();

        assertEquals("acc-123456",transaction.getAccountId());
        assertEquals(TransactionType.CREDIT,transaction.getType());
        assertEquals(new BigDecimal("1500.00"),transaction.getAmount());
        assertEquals(TransactionStatus.EXECUTED,transaction.getStatus());


    }

    @Test
    void filterTransactionsByStatus() {

        TransactionEntity executed = new TransactionEntity();
        executed.setAccountId("acc-1");
        executed.setType(TransactionType.CREDIT);
        executed.setAmount(new BigDecimal("100.00"));
        executed.setCurrency("MXN");
        executed.setStatus(TransactionStatus.EXECUTED);
        executed.setCreatedAt(Instant.now());

        TransactionEntity rejected = new TransactionEntity();
        rejected.setAccountId("acc-2");
        rejected.setType(TransactionType.DEBIT);
        rejected.setAmount(new BigDecimal("200.00"));
        rejected.setCurrency("MXN");
        rejected.setStatus(TransactionStatus.REJECTED);
        rejected.setCreatedAt(Instant.now());

        repository.saveAll(List.of(executed, rejected));

        Specification<TransactionEntity> specification =
                TransactionSpecification.hasStatus(TransactionStatus.EXECUTED);

        Page<TransactionEntity> result =
                repository.findAll(specification, PageRequest.of(0, 20));

        assertEquals(1, result.getTotalElements());
        assertEquals(TransactionStatus.EXECUTED,
                result.getContent().getFirst().getStatus());
    }

    @Test
    void filterTransactionsByAccountStatusAndType() {

        // guarda varias transacciones...

        Specification<TransactionEntity> specification =
                TransactionSpecification.hasAccountId("acc-123")
                        .and(TransactionSpecification.hasStatus(TransactionStatus.EXECUTED))
                        .and(TransactionSpecification.hasType(TransactionType.CREDIT));

        Page<TransactionEntity> result =
                repository.findAll(
                        specification,
                        PageRequest.of(0, 10)
                );

        assertTrue(
                result.getContent().stream().allMatch(t ->
                        t.getAccountId().equals("acc-123")
                                && t.getStatus() == TransactionStatus.EXECUTED
                                && t.getType() == TransactionType.CREDIT
                )
        );
    }

    @Test
    void shouldPaginateTransactions() {

        // guarda 5 transacciones...

        Page<TransactionEntity> result =
                repository.findAll(
                        PageRequest.of(0, 2)
                );

        assertEquals(2, result.getContent().size());
        assertEquals(5, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
    }
}
