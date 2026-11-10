package br.recife.bancodealimentos.seguranca;

import br.recife.bancodealimentos.config.AppProperties;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CriptografiaServiceTest {

    private static final String CHAVE = Base64.getEncoder().encodeToString(new byte[32]);

    private static CriptografiaService servicoCom(String chave) {
        return new CriptografiaService(new AppProperties(
                new AppProperties.Seguranca(chave),
                new AppProperties.RateLimit(30, 10, 120),
                new AppProperties.Bootstrap(null, null)));
    }

    @Test
    void cifraEDecifraSemPerdas() {
        CriptografiaService servico = servicoCom(CHAVE);
        String cifrado = servico.cifrar("52998224725");
        assertThat(cifrado).doesNotContain("52998224725");
        assertThat(servico.decifrar(cifrado)).isEqualTo("52998224725");
    }

    @Test
    void cifrarDuasVezesGeraSaidasDiferentes() {
        CriptografiaService servico = servicoCom(CHAVE);
        assertThat(servico.cifrar("52998224725")).isNotEqualTo(servico.cifrar("52998224725"));
    }

    @Test
    void indiceCegoEhDeterministicoEDistingueValores() {
        CriptografiaService servico = servicoCom(CHAVE);
        assertThat(servico.indiceCego("52998224725")).isEqualTo(servico.indiceCego("52998224725"));
        assertThat(servico.indiceCego("52998224725")).isNotEqualTo(servico.indiceCego("52998224726"));
        assertThat(servico.indiceCego("52998224725")).hasSize(64);
    }

    @Test
    void conteudoAdulteradoNaoDecifra() {
        CriptografiaService servico = servicoCom(CHAVE);
        byte[] bytes = Base64.getDecoder().decode(servico.cifrar("52998224725"));
        bytes[bytes.length - 1] ^= 1;
        String adulterado = Base64.getEncoder().encodeToString(bytes);
        assertThatThrownBy(() -> servico.decifrar(adulterado)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void recusaChaveAusenteOuCurta() {
        assertThatThrownBy(() -> servicoCom(null)).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> servicoCom(" ")).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> servicoCom(Base64.getEncoder().encodeToString(new byte[16])))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> servicoCom("isto-nao-e-base64!!"))
                .isInstanceOf(IllegalStateException.class);
    }
}
