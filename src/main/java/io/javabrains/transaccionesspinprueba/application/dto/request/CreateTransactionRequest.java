package io.javabrains.transaccionesspinprueba.application.dto.request;
import io.javabrains.transaccionesspinprueba.domain.enums.TransactionType;

import java.math.BigDecimal;


public record CreateTransactionRequest(
        String accountId,
        TransactionType type,
        BigDecimal amount,
        String currency,
        String description
) {
}
