package io.javabrains.transaccionesspinprueba.infrastructure.persistence;

import io.javabrains.transaccionesspinprueba.TestcontainersConfiguration;
import io.javabrains.transaccionesspinprueba.domain.enums.TransactionStatus;
import io.javabrains.transaccionesspinprueba.domain.enums.TransactionType;
import io.javabrains.transaccionesspinprueba.infrastructure.persistence.entity.TransactionEntity;
import io.javabrains.transaccionesspinprueba.infrastructure.persistence.repository.TransactionJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public class TransactionJpaRepositoryIT {

    @Autowired
    private TransactionJpaRepository repository;

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

}
