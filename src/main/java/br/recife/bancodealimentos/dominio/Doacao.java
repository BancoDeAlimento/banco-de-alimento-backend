package br.recife.bancodealimentos.dominio;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "doacao")
public class Doacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_doador", nullable = false, length = 120)
    private String nomeDoador;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_doador", nullable = false, length = 20)
    private TipoDoador tipoDoador;

    @Column(nullable = false, length = 120)
    private String contato;

    @Column(name = "descricao_alimentos", nullable = false, length = 500)
    private String descricaoAlimentos;

    @Column(name = "quantidade_estimada_kg", precision = 9, scale = 2)
    private BigDecimal quantidadeEstimadaKg;

    @Column(name = "bairro_ou_local", length = 200)
    private String bairroOuLocal;

    @Column(length = 500)
    private String observacoes;

    @Column(name = "consentimento_em", nullable = false)
    private Instant consentimentoEm;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @PrePersist
    void aoCriar() {
        this.criadoEm = Instant.now();
    }

    public Long getId() { return id; }
    public String getNomeDoador() { return nomeDoador; }
    public void setNomeDoador(String v) { this.nomeDoador = v; }
    public TipoDoador getTipoDoador() { return tipoDoador; }
    public void setTipoDoador(TipoDoador v) { this.tipoDoador = v; }
    public String getContato() { return contato; }
    public void setContato(String v) { this.contato = v; }
    public String getDescricaoAlimentos() { return descricaoAlimentos; }
    public void setDescricaoAlimentos(String v) { this.descricaoAlimentos = v; }
    public BigDecimal getQuantidadeEstimadaKg() { return quantidadeEstimadaKg; }
    public void setQuantidadeEstimadaKg(BigDecimal v) { this.quantidadeEstimadaKg = v; }
    public String getBairroOuLocal() { return bairroOuLocal; }
    public void setBairroOuLocal(String v) { this.bairroOuLocal = v; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String v) { this.observacoes = v; }
    public Instant getConsentimentoEm() { return consentimentoEm; }
    public void setConsentimentoEm(Instant v) { this.consentimentoEm = v; }
    public Instant getCriadoEm() { return criadoEm; }
}