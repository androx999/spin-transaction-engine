package io.javabrains.transaccionesspinprueba;

import org.springframework.boot.SpringApplication;

public class TestTransaccionesspinpruebaApplication {

    public static void main(String[] args) {
        SpringApplication.from(TransaccionesspinpruebaApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
