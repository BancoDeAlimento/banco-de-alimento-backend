package br.recife.bancodealimentos.servico;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Gera códigos curtos de 8 caracteres, sem caracteres ambíguos (sem 0, O, 1, I, L).
 * 31 símbolos ^ 8 posições ≈ 850 bilhões de combinações.
 */
@Component
public class CodigoAtendimentoGenerator {

    public static final String ALFABETO = "ABCDEFGHJKMNPQRSTUVWXYZ23456789";
    public static final int TAMANHO = 8;

    private static final Pattern FORMATO = Pattern.compile("[" + ALFABETO + "]{" + TAMANHO + "}");

    private final SecureRandom random = new SecureRandom();

    public String gerar() {
        StringBuilder sb = new StringBuilder(TAMANHO);
        for (int i = 0; i < TAMANHO; i++) {
            sb.append(ALFABETO.charAt(random.nextInt(ALFABETO.length())));
        }
        return sb.toString();
    }

    /** Aceita minúsculas, espaços e hífen (ex.: "k7qm-r3xw"). */
    public static String normalizar(String informado) {
        if (informado == null) {
            return null;
        }
        return informado.replaceAll("[^A-Za-z0-9]", "").toUpperCase(Locale.ROOT);
    }

    public static boolean formatoValido(String codigo) {
        return codigo != null && FORMATO.matcher(codigo).matches();
    }

    public static String formatarParaExibicao(String codigo) {
        return codigo.substring(0, 4) + "-" + codigo.substring(4);
    }
}
