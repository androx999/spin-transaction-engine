package io.javabrains.transaccionesspinprueba.infrastructure.provider.exception;

public class ProviderRejectedException extends RuntimeException  {
    private final String code;


    public ProviderRejectedException(String code,String message) {
        super(message);
        this.code = code;
    }
}
