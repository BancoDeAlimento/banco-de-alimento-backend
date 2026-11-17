package br.recife.bancodealimentos.web.dto;

import br.recife.bancodealimentos.dominio.FaixaIdade;
import br.recife.bancodealimentos.dominio.Origem;
import br.recife.bancodealimentos.dominio.StatusCadunico;
import br.recife.bancodealimentos.dominio.StatusPedido;
import br.recife.bancodealimentos.dominio.TipoContato;

import java.time.Instant;

/** Visão da equipe (triagem/admin). CPF e NIS nunca saem: só se informa se existem. */
public record PedidoEquipeResponse(
        Long id,
        String codigo,
        StatusPedido status,
        int numPessoas,
        String pontoRetirada,
        Origem origem,
        String preenchidoPor,
        boolean possivelDuplicidade,
        String motivoDuplicidade,
        Instant criadoEm,
        Instant atualizadoEm,
        SolicitanteEquipe solicitante) {

    public record SolicitanteEquipe(
            String nomeOuApelido,
            FaixaIdade faixaIdade,
            String bairroOuReferencia,
            String contato,
            TipoContato tipoContato,
            boolean possuiCpf,
            boolean possuiNis,
            StatusCadunico statusCadunico) {
    }
}
