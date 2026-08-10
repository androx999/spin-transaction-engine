package io.javabrains.transaccionesspinprueba.infrastructure.provider.dto.response;

import io.javabrains.transaccionesspinprueba.infrastructure.provider.enums.ProviderStatus;

public record ProviderErrorResponse(
        ProviderStatus status,
        String code,
        String message
) {
}
