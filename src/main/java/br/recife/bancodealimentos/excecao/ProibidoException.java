package br.recife.bancodealimentos.excecao;

public class ProibidoException extends RuntimeException {
    public ProibidoException() {
        super("Seu perfil não tem permissão para esta operação.");
    }
}
