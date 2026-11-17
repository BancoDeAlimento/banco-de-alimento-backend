package br.recife.bancodealimentos.dominio;

public enum TipoContato {
    TELEFONE,
    WHATSAPP,
    /** Recado via CRAS / Centro POP / entidade parceira. O campo contato guarda o nome do ponto de apoio. */
    RECADO_CRAS_ENTIDADE
}
