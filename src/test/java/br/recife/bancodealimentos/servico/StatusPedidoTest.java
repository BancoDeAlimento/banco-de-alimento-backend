package br.recife.bancodealimentos.servico;

import br.recife.bancodealimentos.dominio.StatusPedido;
import org.junit.jupiter.api.Test;

import static br.recife.bancodealimentos.dominio.StatusPedido.AGUARDANDO_RETIRADA;
import static br.recife.bancodealimentos.dominio.StatusPedido.CANCELADO;
import static br.recife.bancodealimentos.dominio.StatusPedido.EM_ANALISE;
import static br.recife.bancodealimentos.dominio.StatusPedido.RECEBIDO;
import static br.recife.bancodealimentos.dominio.StatusPedido.RETIRADO;
import static org.assertj.core.api.Assertions.assertThat;

class StatusPedidoTest {

    @Test
    void fluxoNormalAvancaUmaEtapaPorVez() {
        assertThat(RECEBIDO.podeIrPara(EM_ANALISE)).isTrue();
        assertThat(EM_ANALISE.podeIrPara(AGUARDANDO_RETIRADA)).isTrue();
        assertThat(AGUARDANDO_RETIRADA.podeIrPara(RETIRADO)).isTrue();
    }

    @Test
    void naoPermitePularEtapasNemVoltar() {
        assertThat(RECEBIDO.podeIrPara(RETIRADO)).isFalse();
        assertThat(RECEBIDO.podeIrPara(AGUARDANDO_RETIRADA)).isFalse();
        assertThat(EM_ANALISE.podeIrPara(RECEBIDO)).isFalse();
    }

    @Test
    void cancelarValeEmQualquerEtapaAberta() {
        assertThat(RECEBIDO.podeIrPara(CANCELADO)).isTrue();
        assertThat(EM_ANALISE.podeIrPara(CANCELADO)).isTrue();
        assertThat(AGUARDANDO_RETIRADA.podeIrPara(CANCELADO)).isTrue();
    }

    @Test
    void estadosFinaisNaoMudamMais() {
        for (StatusPedido destino : StatusPedido.values()) {
            assertThat(RETIRADO.podeIrPara(destino)).isFalse();
            assertThat(CANCELADO.podeIrPara(destino)).isFalse();
        }
        assertThat(RETIRADO.terminal()).isTrue();
        assertThat(CANCELADO.terminal()).isTrue();
        assertThat(RECEBIDO.aberto()).isTrue();
    }
}
