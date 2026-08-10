package io.javabrains.transaccionesspinprueba.application.dto.request;
import io.javabrains.transaccionesspinprueba.domain.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;


public record CreateTransactionRequest(
        @NotBlank(message = "AccountId is required")
        String accountId,
        @NotNull(message = "Transaction type is required")
        TransactionType type,
        @NotNull(message = "Amount is required")
        BigDecimal amount,
        @NotNull(message = "Currency is required")

        String currency,

        String description
) {
}
