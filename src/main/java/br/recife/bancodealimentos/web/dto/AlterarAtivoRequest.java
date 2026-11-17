package br.recife.bancodealimentos.web.dto;

import jakarta.validation.constraints.NotNull;

public record AlterarAtivoRequest(
        @NotNull(message = "Informe se o usuário está ativo.")
        Boolean ativo) {
}
