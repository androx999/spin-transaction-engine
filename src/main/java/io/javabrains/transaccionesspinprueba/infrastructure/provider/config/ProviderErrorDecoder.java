package io.javabrains.transaccionesspinprueba.infrastructure.provider.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import io.javabrains.transaccionesspinprueba.infrastructure.provider.dto.response.ProviderErrorResponse;
import io.javabrains.transaccionesspinprueba.infrastructure.provider.enums.ProviderStatus;
import io.javabrains.transaccionesspinprueba.infrastructure.provider.exception.ProviderRejectedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;

@RequiredArgsConstructor
public class ProviderErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    @Override
    public Exception decode(String methodKey, Response response) {
        try(InputStream body = response.body().asInputStream())
        {
            ProviderErrorResponse error = objectMapper.readValue(body,ProviderErrorResponse.class);

            if(error.status() == ProviderStatus.REJECTED){
                return new ProviderRejectedException(
                        error.code(),
                        error.message()
                );
            }
        } catch (Exception e) {
            return new RuntimeException(
                    "Unable to process provider error response",
                    e
            );
        }
        return new RuntimeException(
                "Unexpected provider error. HTTP status: " + response.status()
        );
    }
}
