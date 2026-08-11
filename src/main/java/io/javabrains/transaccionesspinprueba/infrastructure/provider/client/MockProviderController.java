package io.javabrains.transaccionesspinprueba.infrastructure.provider.client;

import io.javabrains.transaccionesspinprueba.domain.enums.TransactionType;
import io.javabrains.transaccionesspinprueba.infrastructure.provider.dto.request.ProviderTransactionRequest;
import io.javabrains.transaccionesspinprueba.infrastructure.provider.dto.response.ProviderErrorResponse;
import io.javabrains.transaccionesspinprueba.infrastructure.provider.dto.response.ProviderTransactionResponse;
import io.javabrains.transaccionesspinprueba.infrastructure.provider.enums.ProviderStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;

@RestController
@RequestMapping("/provider/v1")
public class MockProviderController {

    @PostMapping("/execute")
    public ResponseEntity<?> execute(
            @RequestBody ProviderTransactionRequest request) {

        // Simulación de rechazo del proveedor
        if (request.type() == TransactionType.DEBIT &&
                request.amount().compareTo(new BigDecimal("5000.00")) > 0) {

            ProviderErrorResponse error =
                    new ProviderErrorResponse(
                            ProviderStatus.REJECTED,
                            "INSUFFICIENT_FUNDS",
                            "Saldo insuficiente"
                    );

            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(error);
        }

        // Simulación de aprobación del proveedor
        ProviderTransactionResponse response =
                new ProviderTransactionResponse(
                        "txn-manual-001",
                        ProviderStatus.APPROVED,
                        new BigDecimal("8000.00"),
                        Instant.now()
                );

        return ResponseEntity.ok(response);
    }
}