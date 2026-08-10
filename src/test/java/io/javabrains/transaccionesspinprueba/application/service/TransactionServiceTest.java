package io.javabrains.transaccionesspinprueba.application.service;

import io.javabrains.transaccionesspinprueba.application.dto.request.CreateTransactionRequest;
import io.javabrains.transaccionesspinprueba.application.dto.response.TransactionResponse;
import io.javabrains.transaccionesspinprueba.domain.enums.TransactionStatus;
import io.javabrains.transaccionesspinprueba.domain.enums.TransactionType;
import io.javabrains.transaccionesspinprueba.domain.model.Transaction;
import io.javabrains.transaccionesspinprueba.domain.repository.TransactionRepository;
import io.javabrains.transaccionesspinprueba.infrastructure.provider.client.TransactionProviderClient;
import io.javabrains.transaccionesspinprueba.infrastructure.provider.dto.request.ProviderTransactionRequest;
import io.javabrains.transaccionesspinprueba.infrastructure.provider.dto.response.ProviderTransactionResponse;
import io.javabrains.transaccionesspinprueba.infrastructure.provider.enums.ProviderStatus;
import io.javabrains.transaccionesspinprueba.infrastructure.provider.exception.ProviderRejectedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionProviderClient providerClient;

    @InjectMocks
    private TransactionService transactionService;


    @Test
    void createExecutedTransactionWhenProviderApproves(){
        //Arrange
        CreateTransactionRequest request = new CreateTransactionRequest(
                "acc-123456",
                TransactionType.CREDIT,
                new BigDecimal("1500.00"),
                "MXN",
                "Transferencia recibida"
        );

        ProviderTransactionResponse providerResponse = new ProviderTransactionResponse(
          "txn-789",
          ProviderStatus.APPROVED,
          new BigDecimal("5500.00"),
          Instant.parse("2026-08-10T15:00:00Z")
        );

        when(providerClient.executeTransaction(any(ProviderTransactionRequest.class)))
                .thenReturn(providerResponse);

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //Act
        TransactionResponse response = transactionService.createTransaction(request);


        //Assert
        assertNotNull(response);
        assertNotNull(response.getId());

        assertEquals("acc-123456", response.getAccountId());
        assertEquals(TransactionType.CREDIT, response.getType());
        assertEquals(new BigDecimal("1500.00"), response.getAmount());
        assertEquals("MXN", response.getCurrency());

        assertEquals(
                TransactionStatus.EXECUTED,
                response.getStatus()
        );

        assertEquals(
                "txn-789",
                response.getProviderTransactionId()
        );

        assertEquals(
                new BigDecimal("5500.00"),
                response.getBalanceAfter()
        );


        verify(providerClient, times(1))
                .executeTransaction(any(ProviderTransactionRequest.class));

        verify(transactionRepository, times(1))
                .save(any(Transaction.class));

    }


    @Test
    void createRejectedTransactionWhenProviderRejects() {

        // Arrange
        CreateTransactionRequest request =
                new CreateTransactionRequest(
                        "acc-123456",
                        TransactionType.DEBIT,
                        new BigDecimal("5000.00"),
                        "MXN",
                        "Compra"
                );

        when(providerClient.executeTransaction(
                any(ProviderTransactionRequest.class)
        )).thenThrow(
                new ProviderRejectedException(
                        "INSUFFICIENT_FUNDS",
                        "Saldo insuficiente"
                )
        );

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TransactionResponse response =
                transactionService.createTransaction(request);

        // Assert
        assertNotNull(response);

        assertEquals(
                TransactionStatus.REJECTED,
                response.getStatus()
        );

        assertNull(response.getProviderTransactionId());
        assertNull(response.getBalanceAfter());

        verify(providerClient, times(1))
                .executeTransaction(any(ProviderTransactionRequest.class));

        verify(transactionRepository, times(1))
                .save(any(Transaction.class));
    }


    @Test
    void rejectAmountLessThanOrEqualToOneWithoutCallingProvider() {

        CreateTransactionRequest request =
                new CreateTransactionRequest(
                        "acc-123456",
                        TransactionType.CREDIT,
                        new BigDecimal("1.00"),
                        "MXN",
                        "Invalid transaction"
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> transactionService.createTransaction(request)
                );

        assertEquals(
                "Amount must be greater than 1.00",
                exception.getMessage()
        );

        verifyNoInteractions(providerClient);
        verifyNoInteractions(transactionRepository);
    }


    @Test
    void rejectDebitGreaterThanTenThousandWithoutCallingProvider() {

        CreateTransactionRequest request =
                new CreateTransactionRequest(
                        "acc-123456",
                        TransactionType.DEBIT,
                        new BigDecimal("10000.01"),
                        "MXN",
                        "Invalid debit"
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> transactionService.createTransaction(request)
                );

        assertEquals(
                "Debit amount cannot exceed 10000.00",
                exception.getMessage()
        );

        verifyNoInteractions(providerClient);
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void rejectNonMxnCurrencyWithoutCallingProvider() {

        CreateTransactionRequest request =
                new CreateTransactionRequest(
                        "acc-123456",
                        TransactionType.CREDIT,
                        new BigDecimal("1500.00"),
                        "USD",
                        "Invalid currency"
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> transactionService.createTransaction(request)
                );

        assertEquals(
                "Only MXN currency is supported",
                exception.getMessage()
        );

        verifyNoInteractions(providerClient);
        verifyNoInteractions(transactionRepository);
    }
}
