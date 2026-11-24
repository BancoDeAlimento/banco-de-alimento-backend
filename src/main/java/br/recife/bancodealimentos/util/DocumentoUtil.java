package br.recife.bancodealimentos.util;

public final class DocumentoUtil {

    private static final int[] PESOS_CNPJ_1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] PESOS_CNPJ_2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    private DocumentoUtil() {
    }

    /** Remove pontuação e deixa em maiúsculo. Aceita CNPJ com letras (formato novo desde julho/2026). */
    public static String normalizar(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replaceAll("[^0-9A-Za-z]", "").toUpperCase();
    }

    public static boolean cpfValido(String normalizado) {
        if (normalizado == null || !normalizado.matches("\\d{11}") || todosIguais(normalizado)) {
            return false;
        }
        int d1 = digitoCpf(normalizado, 9);
        int d2 = digitoCpf(normalizado, 10);
        return d1 == normalizado.charAt(9) - '0' && d2 == normalizado.charAt(10) - '0';
    }

    public static boolean cnpjValido(String normalizado) {
        if (normalizado == null || !normalizado.matches("[0-9A-Z]{12}\\d{2}") || todosIguais(normalizado)) {
            return false;
        }
        int d1 = digitoCnpj(normalizado, PESOS_CNPJ_1);
        int d2 = digitoCnpj(normalizado, PESOS_CNPJ_2);
        return d1 == normalizado.charAt(12) - '0' && d2 == normalizado.charAt(13) - '0';
    }

    private static int digitoCpf(String cpf, int quantidade) {
        int soma = 0;
        for (int i = 0; i < quantidade; i++) {
            soma += (cpf.charAt(i) - '0') * (quantidade + 1 - i);
        }
        int resto = (soma * 10) % 11;
        return resto == 10 ? 0 : resto;
    }

    private static int digitoCnpj(String cnpj, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < pesos.length; i++) {
            soma += (cnpj.charAt(i) - '0') * pesos[i]; // letra A vale 17, B vale 18... (ASCII - 48)
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    private static boolean todosIguais(String texto) {
        return texto.chars().distinct().count() == 1;
    }
}