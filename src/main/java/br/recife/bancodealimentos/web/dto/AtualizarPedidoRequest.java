package br.recife.bancodealimentos.web.dto;

import br.recife.bancodealimentos.dominio.StatusPedido;
import jakarta.validation.constraints.Size;

/** Informe o novo status, o ponto de retirada, ou ambos. */
public record AtualizarPedidoRequest(
        StatusPedido status,
        @Size(max = 200, message = "Use até 200 caracteres.")
        String pontoRetirada) {
}
