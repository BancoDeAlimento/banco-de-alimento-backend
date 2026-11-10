package br.recife.bancodealimentos.web.dto;

import br.recife.bancodealimentos.dominio.Perfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CriarUsuarioRequest(
        @NotBlank(message = "Informe o nome.")
        @Size(max = 120)
        String nome,

        @NotBlank(message = "Informe o e-mail.")
        @Email(message = "E-mail inválido.")
        @Size(max = 160)
        String email,

        @NotBlank(message = "Informe a senha.")
        @Size(min = 10, max = 72, message = "A senha deve ter entre 10 e 72 caracteres.")
        String senha,

        @NotNull(message = "Informe o perfil.")
        Perfil perfil) {
}
