package io.javabrains.transaccionesspinprueba.infrastructure.provider.client;

import io.javabrains.transaccionesspinprueba.domain.enums.TransactionType;
import io.javabrains.transaccionesspinprueba.infrastructure.provider.config.TransactionProviderFeignConfig;
import io.javabrains.transaccionesspinprueba.infrastructure.provider.dto.request.ProviderTransactionRequest;
import io.javabrains.transaccionesspinprueba.infrastructure.provider.dto.response.ProviderTransactionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;

/**
        * Contract for communicating with the external provider
 * (POST /provider/v1/execute).

 */
@FeignClient(name="transaction-provider",url="${provider.transaction.url}",configuration = TransactionProviderFeignConfig.class
)
public interface TransactionProviderClient {
    @PostMapping("/provider/v1/execute")
    ProviderTransactionResponse executeTransaction(
            @RequestBody ProviderTransactionRequest request);
}
