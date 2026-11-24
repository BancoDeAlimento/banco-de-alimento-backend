package br.recife.bancodealimentos.dominio;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "entidade")
public class Entidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoEntidade tipo;

    /** CNPJ ou CPF cifrado (AES-GCM). Nunca em texto puro. */
    @Column(name = "documento_cifrado", nullable = false, length = 200)
    private String documentoCifrado;

    /** HMAC do documento: permite achar duplicidade sem decifrar. */
    @Column(name = "documento_indice", nullable = false, length = 64)
    private String documentoIndice;

    @Column(nullable = false, length = 120)
    private String contato;

    @Column(nullable = false, length = 120)
    private String bairro;

    @Column(name = "descricao_atividades", length = 500)
    private String descricaoAtividades;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_credenciamento", nullable = false, length = 20)
    private StatusCredenciamento status = StatusCredenciamento.PENDENTE;

    @Column(name = "possivel_duplicidade", nullable = false)
    private boolean possivelDuplicidade;

    @Column(name = "motivo_duplicidade", length = 200)
    private String motivoDuplicidade;

    @Column(name = "consentimento_em", nullable = false)
    private Instant consentimentoEm;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @PrePersist
    void aoCriar() {
        this.criadoEm = Instant.now();
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String v) { this.nome = v; }
    public TipoEntidade getTipo() { return tipo; }
    public void setTipo(TipoEntidade v) { this.tipo = v; }
    public String getDocumentoCifrado() { return documentoCifrado; }
    public void setDocumentoCifrado(String v) { this.documentoCifrado = v; }
    public String getDocumentoIndice() { return documentoIndice; }
    public void setDocumentoIndice(String v) { this.documentoIndice = v; }
    public String getContato() { return contato; }
    public void setContato(String v) { this.contato = v; }
    public String getBairro() { return bairro; }
    public void setBairro(String v) { this.bairro = v; }
    public String getDescricaoAtividades() { return descricaoAtividades; }
    public void setDescricaoAtividades(String v) { this.descricaoAtividades = v; }
    public StatusCredenciamento getStatus() { return status; }
    public void setStatus(StatusCredenciamento v) { this.status = v; }
    public boolean isPossivelDuplicidade() { return possivelDuplicidade; }
    public void setPossivelDuplicidade(boolean v) { this.possivelDuplicidade = v; }
    public String getMotivoDuplicidade() { return motivoDuplicidade; }
    public void setMotivoDuplicidade(String v) { this.motivoDuplicidade = v; }
    public Instant getConsentimentoEm() { return consentimentoEm; }
    public void setConsentimentoEm(Instant v) { this.consentimentoEm = v; }
    public Instant getCriadoEm() { return criadoEm; }
}