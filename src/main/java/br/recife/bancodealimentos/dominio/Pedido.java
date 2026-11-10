package br.recife.bancodealimentos.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solicitante_id", nullable = false)
    private Solicitante solicitante;

    @Column(name = "num_pessoas", nullable = false)
    private int numPessoas;

    @Column(name = "codigo_atendimento", nullable = false, unique = true, length = 12)
    private String codigoAtendimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private StatusPedido status;

    @Column(name = "ponto_retirada", length = 200)
    private String pontoRetirada;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private Origem origem;

    /** Quem preencheu, quando o atendimento foi assistido (nome do usuario ou do servico informado). */
    @Column(name = "preenchido_por", length = 120)
    private String preenchidoPor;

    /** Alerta para a equipe revisar. Nunca causa recusa automatica. */
    @Column(name = "possivel_duplicidade", nullable = false)
    private boolean possivelDuplicidade;

    @Column(name = "motivo_duplicidade", length = 255)
    private String motivoDuplicidade;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    public Pedido() {
    }

    @PrePersist
    void aoCriar() {
        Instant agora = Instant.now();
        this.criadoEm = agora;
        this.atualizadoEm = agora;
    }

    @PreUpdate
    void aoAtualizar() {
        this.atualizadoEm = Instant.now();
    }

    public Long getId() { return id; }

    public Solicitante getSolicitante() { return solicitante; }
    public void setSolicitante(Solicitante solicitante) { this.solicitante = solicitante; }

    public int getNumPessoas() { return numPessoas; }
    public void setNumPessoas(int numPessoas) { this.numPessoas = numPessoas; }

    public String getCodigoAtendimento() { return codigoAtendimento; }
    public void setCodigoAtendimento(String codigoAtendimento) { this.codigoAtendimento = codigoAtendimento; }

    public StatusPedido getStatus() { return status; }
    public void setStatus(StatusPedido status) { this.status = status; }

    public String getPontoRetirada() { return pontoRetirada; }
    public void setPontoRetirada(String pontoRetirada) { this.pontoRetirada = pontoRetirada; }

    public Origem getOrigem() { return origem; }
    public void setOrigem(Origem origem) { this.origem = origem; }

    public String getPreenchidoPor() { return preenchidoPor; }
    public void setPreenchidoPor(String preenchidoPor) { this.preenchidoPor = preenchidoPor; }

    public boolean isPossivelDuplicidade() { return possivelDuplicidade; }
    public void setPossivelDuplicidade(boolean possivelDuplicidade) { this.possivelDuplicidade = possivelDuplicidade; }

    public String getMotivoDuplicidade() { return motivoDuplicidade; }
    public void setMotivoDuplicidade(String motivoDuplicidade) { this.motivoDuplicidade = motivoDuplicidade; }

    public Instant getCriadoEm() { return criadoEm; }
    public Instant getAtualizadoEm() { return atualizadoEm; }
}
