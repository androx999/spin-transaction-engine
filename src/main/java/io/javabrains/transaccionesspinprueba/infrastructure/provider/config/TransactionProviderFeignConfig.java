package io.javabrains.transaccionesspinprueba.infrastructure.provider.config;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.ObjectMapper;

public class TransactionProviderFeignConfig {
    @Bean
    ErrorDecoder providerErrorDecoder(ObjectMapper objectMapper) {
        return new ProviderErrorDecoder(objectMapper);
    }
}
