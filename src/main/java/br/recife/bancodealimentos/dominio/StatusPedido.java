package br.recife.bancodealimentos.dominio;

public enum StatusPedido {
    RECEBIDO,
    EM_ANALISE,
    AGUARDANDO_RETIRADA,
    RETIRADO,
    CANCELADO;

    /** Pedido que ainda pode gerar duplicidade / ser atendido. */
    public boolean aberto() {
        return this == RECEBIDO || this == EM_ANALISE || this == AGUARDANDO_RETIRADA;
    }

    public boolean terminal() {
        return !aberto();
    }

    /** Fluxo: recebido -> em_analise -> aguardando_retirada -> retirado. Cancelar vale em qualquer etapa aberta. */
    public boolean podeIrPara(StatusPedido destino) {
        return switch (this) {
            case RECEBIDO -> destino == EM_ANALISE || destino == CANCELADO;
            case EM_ANALISE -> destino == AGUARDANDO_RETIRADA || destino == CANCELADO;
            case AGUARDANDO_RETIRADA -> destino == RETIRADO || destino == CANCELADO;
            case RETIRADO, CANCELADO -> false;
        };
    }
}
