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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionProviderClient providerClient;

    public TransactionResponse createTransaction(CreateTransactionRequest request)
    {
        validateRequest(request);

        ProviderTransactionRequest providerRequest = new ProviderTransactionRequest(
                request.accountId(),
                request.type(),
                request.amount(),
                request.currency()
        );
        try {
            ProviderTransactionResponse providerResponse = providerClient.executeTransaction(providerRequest);

            Transaction transaction = buildExecutedTransaction(request, providerResponse);

            Transaction saved = transactionRepository.save(transaction);

            return toResponse(saved);

        }catch (ProviderRejectedException ex) {
            Transaction transaction = buildRejectedTransaction(request,ex);
            Transaction saved =
                    transactionRepository.save(transaction);

            return toResponse(saved);
        }



    }

    private Transaction buildRejectedTransaction(CreateTransactionRequest request, ProviderRejectedException ex) {
        return new Transaction(
                null,
                        request.accountId(),
                        request.type(),
                        request.amount(),
                        request.currency(),
                        request.description(),
                        TransactionStatus.REJECTED,
                        null,
                        null,
                Instant.now(),
                ex.getMessage()
                );
    }

    private TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAccountId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getDescription(),
                transaction.getStatus(),
                transaction.getProviderTransactionId(),
                transaction.getBalanceAfter(),
                transaction.getCreatedAt()
        );
    }

    private Transaction buildExecutedTransaction(CreateTransactionRequest request, ProviderTransactionResponse providerResponse) {
        if (providerResponse.status() != ProviderStatus.APPROVED) {
            throw new IllegalStateException(
                    "Unexpected provider status: " + providerResponse.status()
            );
        }
        return new Transaction(
                null,
                request.accountId(),
                request.type(),
                request.amount(),
                request.currency(),
                request.description(),
                TransactionStatus.EXECUTED,
                providerResponse.transactionId(),
                providerResponse.balance(),
                providerResponse.executedAt(),

        null

        );
    }
    private void validateRequest(CreateTransactionRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Request is required");
        }

        if (request.accountId() == null ||
                request.accountId().isBlank()) {
            throw new IllegalArgumentException(
                    "AccountId is required"
            );
        }

        if (request.type() == null) {
            throw new IllegalArgumentException(
                    "Transaction type is required"
            );
        }

        if (request.amount() == null ||
                request.amount().compareTo(new BigDecimal("1.00")) <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be greater than 1.00"
            );
        }

        if (request.type() == TransactionType.DEBIT &&
                request.amount().compareTo(new BigDecimal("10000.00")) > 0) {
            throw new IllegalArgumentException(
                    "Debit amount cannot exceed 10000.00"
            );
        }

        if (!"MXN".equalsIgnoreCase(request.currency())) {
            throw new IllegalArgumentException(
                    "Only MXN currency is supported"
            );
        }
    }


    public Page<TransactionResponse> getTranscations(String accountId,TransactionStatus status,TransactionType type,int page,int limit){

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page must be greater than or equal to 0"
            );
        }

        if (limit < 1 || limit > 100) {
            throw new IllegalArgumentException(
                    "Limit must be between 1 and 100"
            );
        }

        Pageable pageable = PageRequest.of(page,limit, Sort.by(Sort.Direction.DESC,"createdAt"));

        return transactionRepository.findAll(accountId,status,type,pageable).map(this::toResponse);
    }
}
