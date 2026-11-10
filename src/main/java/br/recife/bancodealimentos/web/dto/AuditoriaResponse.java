package br.recife.bancodealimentos.web.dto;

import java.time.Instant;

public record AuditoriaResponse(
        Long id,
        Long usuarioId,
        String acao,
        String tabelaAfetada,
        Long registroId,
        Instant ocorridoEm) {
}
