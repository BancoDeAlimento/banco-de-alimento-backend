package br.recife.bancodealimentos.web.dto;

import br.recife.bancodealimentos.dominio.StatusPedido;

public record PedidoCriadoResponse(
        String codigo,
        String codigoExibicao,
        StatusPedido status,
        String mensagem) {
}
