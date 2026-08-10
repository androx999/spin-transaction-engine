package io.javabrains.transaccionesspinprueba.infrastructure.web.controller;

import io.javabrains.transaccionesspinprueba.application.dto.request.CreateTransactionRequest;
import io.javabrains.transaccionesspinprueba.application.dto.response.TransactionResponse;
import io.javabrains.transaccionesspinprueba.application.service.TransactionService;
import io.javabrains.transaccionesspinprueba.domain.enums.TransactionStatus;
import io.javabrains.transaccionesspinprueba.domain.enums.TransactionType;

import io.javabrains.transaccionesspinprueba.infrastructure.web.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Import(GlobalExceptionHandler.class)
@WebMvcTest(TransactionController.class)
 class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService transactionService;

    @Test
    void createTransactionAndReturn201() throws Exception {

        CreateTransactionRequest request =
                new CreateTransactionRequest(
                        "acc-123456",
                        TransactionType.CREDIT,
                        new BigDecimal("1500.00"),
                        "MXN",
                        "Transferencia recibida"
                );

        TransactionResponse response =
                new TransactionResponse(
                        UUID.randomUUID(),
                        "acc-123456",
                        TransactionType.CREDIT,
                        new BigDecimal("1500.00"),
                        "MXN",
                        "Transferencia recibida",
                        TransactionStatus.EXECUTED,
                        "txn-789",
                        new BigDecimal("5500.00"),
                        Instant.parse("2026-08-10T15:00:00Z")
                );

        when(transactionService.createTransaction(any()))
                .thenReturn(response);

        mockMvc.perform(
                        post("/transactions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").value("acc-123456"))
                .andExpect(jsonPath("$.type").value("CREDIT"))
                .andExpect(jsonPath("$.amount").value(1500.00))
                .andExpect(jsonPath("$.currency").value("MXN"))
                .andExpect(jsonPath("$.status").value("EXECUTED"))
                .andExpect(jsonPath("$.providerTransactionId").value("txn-789"));

        verify(transactionService, times(1))
                .createTransaction(any());
    }

    @Test
    void return400WhenAccountIdIsBlank() throws Exception {

        String request = """
            {
              "accountId": "",
              "type": "CREDIT",
              "amount": 1500.00,
              "currency": "MXN",
              "description": "Transferencia"
            }
            """;

        mockMvc.perform(
                        post("/transactions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("AccountId is required"));

        verifyNoInteractions(transactionService);
    }


    @Test
    void return400WhenBusinessValidationFails() throws Exception {

        when(transactionService.createTransaction(any()))
                .thenThrow(
                        new IllegalArgumentException(
                                "Debit amount cannot exceed 10000.00"
                        )
                );

        String request = """
            {
              "accountId": "acc-123456",
              "type": "DEBIT",
              "amount": 10000.01,
              "currency": "MXN",
              "description": "Compra"
            }
            """;

        mockMvc.perform(
                        post("/transactions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message")
                        .value("Debit amount cannot exceed 10000.00"));
    }
}
