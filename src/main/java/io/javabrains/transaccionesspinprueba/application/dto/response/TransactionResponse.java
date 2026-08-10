package io.javabrains.transaccionesspinprueba.application.dto.response;

import io.javabrains.transaccionesspinprueba.domain.enums.TransactionStatus;
import io.javabrains.transaccionesspinprueba.domain.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Result of executing a transaction against the external provider.
 *
 * Mirrors the provider contract:
 * - Success: { transactionId, status: APPROVED, balance, executedAt }
 * - Failure (4XX-5XX): { status: REJECTED, code, message }
 */
@Data
@AllArgsConstructor
public class TransactionResponse {
     UUID id;

     String accountId;
     TransactionType type;
     BigDecimal amount;
     String currency;
     String description;

     TransactionStatus status;
     String providerTransactionId;
     BigDecimal balanceAfter;

     Instant createdAt;
}
