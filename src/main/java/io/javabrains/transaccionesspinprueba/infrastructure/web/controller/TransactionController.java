package io.javabrains.transaccionesspinprueba.infrastructure.web.controller;

import io.javabrains.transaccionesspinprueba.application.dto.request.CreateTransactionRequest;
import io.javabrains.transaccionesspinprueba.application.dto.response.TransactionResponse;
import io.javabrains.transaccionesspinprueba.application.service.TransactionService;
import io.javabrains.transaccionesspinprueba.domain.enums.TransactionStatus;
import io.javabrains.transaccionesspinprueba.domain.enums.TransactionType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody CreateTransactionRequest request
            ){
        TransactionResponse response = transactionService.createTransaction(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getTransactions(
            @RequestParam(required = false) String accountId,
            @RequestParam(required = false)TransactionStatus status,
            @RequestParam(required = false)TransactionType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit
            ) {

        Page<TransactionResponse> response =
                transactionService.getTranscations(accountId,status,type,page,limit);
        return ResponseEntity.ok(response);
    }
}
