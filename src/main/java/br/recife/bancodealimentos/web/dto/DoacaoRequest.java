package br.recife.bancodealimentos.web.dto;

import br.recife.bancodealimentos.dominio.TipoDoador;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record DoacaoRequest(
        @NotBlank(message = "Informe o nome de quem vai doar.")
        @Size(max = 120, message = "Use até 120 caracteres.")
        String nomeDoador,

        @NotNull(message = "Informe se é pessoa, empresa ou feira.")
        TipoDoador tipoDoador,

        @NotBlank(message = "Informe um contato para combinarmos a retirada.")
        @Size(max = 120, message = "Use até 120 caracteres.")
        String contato,

        @NotBlank(message = "Descreva o que será doado.")
        @Size(max = 500, message = "Use até 500 caracteres.")
        String descricaoAlimentos,

        @DecimalMin(value = "0.1", message = "A quantidade deve ser maior que zero.")
        @Digits(integer = 7, fraction = 2, message = "Quantidade inválida.")
        BigDecimal quantidadeEstimadaKg,

        @Size(max = 200, message = "Use até 200 caracteres.")
        String bairroOuLocal,

        @Size(max = 500, message = "Use até 500 caracteres.")
        String observacoes,

        @NotNull(message = "Informe a concordância sobre o uso dos dados.")
        @AssertTrue(message = "É necessário concordar com o uso dos dados.")
        Boolean consentimentoLgpd) {
}