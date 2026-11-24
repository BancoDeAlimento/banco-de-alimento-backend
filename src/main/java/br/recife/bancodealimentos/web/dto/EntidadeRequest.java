package br.recife.bancodealimentos.web.dto;

import br.recife.bancodealimentos.dominio.TipoEntidade;
import jakarta.validation.constraints.*;

public record EntidadeRequest(
        @NotBlank(message = "Informe o nome da entidade.")
        @Size(max = 150, message = "Use até 150 caracteres.")
        String nome,

        @NotNull(message = "Informe se é OSC ou cozinha solidária.")
        TipoEntidade tipo,

        @NotBlank(message = "Informe o CNPJ (ou CPF, se não houver CNPJ).")
        @Size(max = 30, message = "Documento inválido.")
        String documento,

        @NotBlank(message = "Informe um contato.")
        @Size(max = 120, message = "Use até 120 caracteres.")
        String contato,

        @NotBlank(message = "Informe o bairro.")
        @Size(max = 120, message = "Use até 120 caracteres.")
        String bairro,

        @Size(max = 500, message = "Use até 500 caracteres.")
        String descricaoAtividades,

        @NotNull(message = "Informe a concordância sobre o uso dos dados.")
        @AssertTrue(message = "É necessário concordar com o uso dos dados.")
        Boolean consentimentoLgpd) {
}