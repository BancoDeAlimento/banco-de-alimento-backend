package br.recife.bancodealimentos.servico;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CodigoAtendimentoGeneratorTest {

    private final CodigoAtendimentoGenerator gerador = new CodigoAtendimentoGenerator();

    @Test
    void geraCodigoCurtoSemCaracteresAmbiguos() {
        for (int i = 0; i < 2_000; i++) {
            String codigo = gerador.gerar();
            assertThat(codigo).hasSize(CodigoAtendimentoGenerator.TAMANHO);
            assertThat(codigo).doesNotContain("0", "O", "1", "I", "L");
            assertThat(CodigoAtendimentoGenerator.formatoValido(codigo)).isTrue();
        }
    }

    @Test
    void geraCodigosDiferentes() {
        Set<String> codigos = new HashSet<>();
        for (int i = 0; i < 2_000; i++) {
            codigos.add(gerador.gerar());
        }
        assertThat(codigos).hasSize(2_000);
    }

    @Test
    void normalizaEntradaDigitadaPelaPessoa() {
        assertThat(CodigoAtendimentoGenerator.normalizar(" k7qm-r3xw ")).isEqualTo("K7QMR3XW");
        assertThat(CodigoAtendimentoGenerator.formatoValido("K7QMR3XW")).isTrue();
        assertThat(CodigoAtendimentoGenerator.formatoValido("K7QMR3X0")).isFalse();
        assertThat(CodigoAtendimentoGenerator.formatoValido("ABC")).isFalse();
        assertThat(CodigoAtendimentoGenerator.formatoValido(null)).isFalse();
    }

    @Test
    void formataParaExibicao() {
        assertThat(CodigoAtendimentoGenerator.formatarParaExibicao("K7QMR3XW")).isEqualTo("K7QM-R3XW");
    }
}
