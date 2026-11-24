package br.recife.bancodealimentos.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentoUtilTest {

    @Test
    void validaCpf() {
        assertThat(DocumentoUtil.cpfValido("52998224725")).isTrue();
        assertThat(DocumentoUtil.cpfValido("52998224726")).isFalse();
        assertThat(DocumentoUtil.cpfValido("11111111111")).isFalse();
    }

    @Test
    void validaCnpjNumerico() {
        assertThat(DocumentoUtil.cnpjValido(DocumentoUtil.normalizar("11.222.333/0001-81"))).isTrue();
        assertThat(DocumentoUtil.cnpjValido("11222333000182")).isFalse();
        assertThat(DocumentoUtil.cnpjValido("00000000000000")).isFalse();
    }

    @Test
    void validaCnpjAlfanumerico() {
        assertThat(DocumentoUtil.cnpjValido(DocumentoUtil.normalizar("12.ABC.345/01DE-35"))).isTrue();
        assertThat(DocumentoUtil.cnpjValido("00000000E08G12")).isTrue();
        assertThat(DocumentoUtil.cnpjValido("12ABC34501DE36")).isFalse();
    }
}