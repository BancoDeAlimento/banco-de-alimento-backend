package br.recife.bancodealimentos.web.dto;

import br.recife.bancodealimentos.dominio.StatusPedido;

import java.time.Instant;

/** Resposta pública: nenhum dado pessoal ou de contato. */
public record PedidoStatusResponse(
        String codigo,
        StatusPedido status,
        String pontoRetirada,
        Instant atualizadoEm) {
}
