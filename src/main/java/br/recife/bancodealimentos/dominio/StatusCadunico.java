package br.recife.bancodealimentos.dominio;

public enum StatusCadunico {
    /** A pessoa disse que esta no CadUnico (nao verificado). */
    INFORMADO,
    /** A pessoa disse que nao esta no CadUnico. */
    NAO_ESTA,
    NAO_SEI,
    /** Confirmado internamente por gestor autorizado da Secretaria. */
    VERIFICADO
}
