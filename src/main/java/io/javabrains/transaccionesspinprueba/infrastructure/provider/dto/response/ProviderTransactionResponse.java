package io.javabrains.transaccionesspinprueba.infrastructure.provider.dto.response;

import io.javabrains.transaccionesspinprueba.infrastructure.provider.enums.ProviderStatus;

import java.math.BigDecimal;
import java.time.Instant;


public record ProviderTransactionResponse(
        String transactionId,
        ProviderStatus status,
        BigDecimal balance,
        Instant executedAt

) {
}
