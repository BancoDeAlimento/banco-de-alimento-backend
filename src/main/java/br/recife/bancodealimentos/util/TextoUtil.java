package br.recife.bancodealimentos.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public final class TextoUtil {

    private static final Pattern DIACRITICOS = Pattern.compile("\\p{M}+");
    private static final Pattern ESPACOS = Pattern.compile("\\s+");
    private static final Pattern NAO_DIGITOS = Pattern.compile("\\D");

    private TextoUtil() {
    }

    /** Minusculo, sem acentos, sem espacos repetidos. Usado para comparar nomes e bairros. */
    public static String normalizar(String texto) {
        if (texto == null) {
            return null;
        }
        String semAcento = DIACRITICOS.matcher(Normalizer.normalize(texto.strip(), Normalizer.Form.NFD)).replaceAll("");
        return ESPACOS.matcher(semAcento.toLowerCase(Locale.ROOT)).replaceAll(" ");
    }

    public static String soDigitos(String texto) {
        return texto == null ? null : NAO_DIGITOS.matcher(texto).replaceAll("");
    }

    public static String vazioParaNulo(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        return texto.strip();
    }
}
