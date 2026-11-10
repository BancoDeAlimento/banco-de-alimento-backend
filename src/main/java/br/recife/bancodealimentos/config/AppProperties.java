package br.recife.bancodealimentos.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        @DefaultValue Seguranca seguranca,
        @DefaultValue RateLimit rateLimit,
        @DefaultValue Bootstrap bootstrap) {

    /** Chave mestra em Base64 (minimo 32 bytes). Dela derivam a chave de cifra e a de indice. */
    public record Seguranca(String chaveCriptografia) {
    }

    public record RateLimit(
            @DefaultValue("30") int consultaPorMinuto,
            @DefaultValue("10") int criacaoPorMinuto,
            @DefaultValue("120") int equipePorMinuto) {
    }

    /** Administrador criado na primeira execucao, quando nao existe nenhum usuario. */
    public record Bootstrap(String adminEmail, String adminSenha) {
    }
}
