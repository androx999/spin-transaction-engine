package io.javabrains.transaccionesspinprueba.domain.model;

import io.javabrains.transaccionesspinprueba.domain.enums.TransactionStatus;
import io.javabrains.transaccionesspinprueba.domain.enums.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
@AllArgsConstructor
public class Transaction {
    private UUID id;

    private String accountId;
    private TransactionType type;
    private BigDecimal amount;
    private String currency;
    private String description;

    private TransactionStatus status;
    private String providerTransactionId;
    private BigDecimal balanceAfter;

    private Instant createdAt;
}
