package br.recife.bancodealimentos.excecao;

public class LimiteExcedidoException extends RuntimeException {
    public LimiteExcedidoException() {
        super("Muitas requisições. Aguarde um minuto e tente novamente.");
    }
}
