package br.recife.bancodealimentos.excecao;

public class NaoAutenticadoException extends RuntimeException {
    public NaoAutenticadoException() {
        super("Credenciais ausentes ou inválidas.");
    }
}
