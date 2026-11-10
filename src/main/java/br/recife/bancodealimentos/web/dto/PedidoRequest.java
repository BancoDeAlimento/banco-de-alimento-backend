package br.recife.bancodealimentos.web.dto;

import br.recife.bancodealimentos.dominio.FaixaIdade;
import br.recife.bancodealimentos.dominio.TipoContato;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Obrigatórios: nome/apelido, bairro ou ponto de referência, número de pessoas e consentimento.
 * Todo o resto é opcional. CPF/NIS podem ser omitidos sem prejuízo.
 */
public record PedidoRequest(
        @NotBlank(message = "Informe seu nome ou apelido.")
        @Size(max = 120, message = "Use até 120 caracteres.")
        String nomeOuApelido,

        @NotBlank(message = "Informe o bairro/região ou um ponto de referência.")
        @Size(max = 200, message = "Use até 200 caracteres.")
        String bairroOuReferencia,

        @NotNull(message = "Informe quantas pessoas dependem do pedido.")
        @Min(value = 1, message = "Informe pelo menos 1 pessoa.")
        @Max(value = 30, message = "Máximo de 30 pessoas por pedido.")
        Integer numPessoas,

        FaixaIdade faixaIdade,

        @Size(max = 120, message = "Use até 120 caracteres.")
        String contato,

        TipoContato tipoContato,

        @Size(max = 20, message = "CPF inválido. Deixe em branco se não tiver.")
        String cpf,

        @Size(max = 20, message = "NIS inválido. Deixe em branco se não tiver.")
        String nis,

        RespostaCadunico cadunico,

        /** Preenchimento assistido sem login: nome do CRAS / Centro POP / entidade. */
        @Size(max = 120, message = "Use até 120 caracteres.")
        String preenchidoPor,



        @NotNull(message = "Informe a concordância sobre o uso dos dados.")
        @AssertTrue(message = "É necessário concordar com o uso dos dados.")
        Boolean consentimentoLgpd) {
}
