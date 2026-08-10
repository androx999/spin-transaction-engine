package io.javabrains.transaccionesspinprueba.infrastructure.provider.dto.request;

import io.javabrains.transaccionesspinprueba.domain.enums.TransactionType;

import java.math.BigDecimal;

public record ProviderTransactionRequest(
         String accountId,
         TransactionType type,
         BigDecimal amount,
         String currency
) {
}
