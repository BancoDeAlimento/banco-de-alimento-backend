package br.recife.bancodealimentos.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nulo quando a acao foi feita por alguem sem login (ex.: pessoa pedindo alimento pelo site). */
    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(nullable = false, length = 60)
    private String acao;

    @Column(name = "tabela_afetada", nullable = false, length = 60)
    private String tabelaAfetada;

    @Column(name = "registro_id")
    private Long registroId;

    @Column(name = "ocorrido_em", nullable = false)
    private Instant ocorridoEm;

    protected Auditoria() {
    }

    public Auditoria(Long usuarioId, String acao, String tabelaAfetada, Long registroId) {
        this.usuarioId = usuarioId;
        this.acao = acao;
        this.tabelaAfetada = tabelaAfetada;
        this.registroId = registroId;
        this.ocorridoEm = Instant.now();
    }

    public Long getId() { return id; }
    public Long getUsuarioId() { return usuarioId; }
    public String getAcao() { return acao; }
    public String getTabelaAfetada() { return tabelaAfetada; }
    public Long getRegistroId() { return registroId; }
    public Instant getOcorridoEm() { return ocorridoEm; }
}
