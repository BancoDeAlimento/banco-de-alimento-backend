package br.recife.bancodealimentos.seguranca;

import br.recife.bancodealimentos.dominio.Perfil;

public record UsuarioAutenticado(Long id, String nome, Perfil perfil) {
}
