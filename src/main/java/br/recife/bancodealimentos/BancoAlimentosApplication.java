package br.recife.bancodealimentos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BancoAlimentosApplication {

    public static void main(String[] args) {
        SpringApplication.run(BancoAlimentosApplication.class, args);
    }
}
