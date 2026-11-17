package br.recife.bancodealimentos.web.dto;

import br.recife.bancodealimentos.dominio.Perfil;

import java.time.Instant;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        Perfil perfil,
        boolean ativo,
        Instant criadoEm) {
}
