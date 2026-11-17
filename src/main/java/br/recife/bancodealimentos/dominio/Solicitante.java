package br.recife.bancodealimentos.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "solicitante")
public class Solicitante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_ou_apelido", nullable = false, length = 120)
    private String nomeOuApelido;

    /** Texto normalizado (sem acento, minusculo) usado apenas para detectar duplicidades. */
    @Column(name = "nome_normalizado", nullable = false, length = 120)
    private String nomeNormalizado;

    @Enumerated(EnumType.STRING)
    @Column(name = "faixa_idade", length = 20)
    private FaixaIdade faixaIdade;

    @Column(name = "bairro_ou_referencia", nullable = false, length = 200)
    private String bairroOuReferencia;

    @Column(name = "bairro_normalizado", nullable = false, length = 200)
    private String bairroNormalizado;

    @Column(name = "contato", length = 120)
    private String contato;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_contato", length = 30)
    private TipoContato tipoContato;

    /** CPF cifrado (AES-GCM). Nunca em texto puro. */
    @Column(name = "cpf_cifrado", length = 200)
    private String cpfCifrado;

    /** HMAC do CPF: permite achar duplicidade sem decifrar. */
    @Column(name = "cpf_indice", length = 64)
    private String cpfIndice;

    @Column(name = "nis_cifrado", length = 200)
    private String nisCifrado;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_cadunico", length = 20)
    private StatusCadunico statusCadunico;

    @Column(name = "consentimento_lgpd", nullable = false)
    private boolean consentimentoLgpd;

    @Column(name = "consentimento_em", nullable = false)
    private Instant consentimentoEm;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    public Solicitante() {
    }

    @PrePersist
    void aoCriar() {
        this.criadoEm = Instant.now();
    }

    public Long getId() { return id; }

    public String getNomeOuApelido() { return nomeOuApelido; }
    public void setNomeOuApelido(String nomeOuApelido) { this.nomeOuApelido = nomeOuApelido; }

    public String getNomeNormalizado() { return nomeNormalizado; }
    public void setNomeNormalizado(String nomeNormalizado) { this.nomeNormalizado = nomeNormalizado; }

    public FaixaIdade getFaixaIdade() { return faixaIdade; }
    public void setFaixaIdade(FaixaIdade faixaIdade) { this.faixaIdade = faixaIdade; }

    public String getBairroOuReferencia() { return bairroOuReferencia; }
    public void setBairroOuReferencia(String bairroOuReferencia) { this.bairroOuReferencia = bairroOuReferencia; }

    public String getBairroNormalizado() { return bairroNormalizado; }
    public void setBairroNormalizado(String bairroNormalizado) { this.bairroNormalizado = bairroNormalizado; }

    public String getContato() { return contato; }
    public void setContato(String contato) { this.contato = contato; }

    public TipoContato getTipoContato() { return tipoContato; }
    public void setTipoContato(TipoContato tipoContato) { this.tipoContato = tipoContato; }

    public String getCpfCifrado() { return cpfCifrado; }
    public void setCpfCifrado(String cpfCifrado) { this.cpfCifrado = cpfCifrado; }

    public String getCpfIndice() { return cpfIndice; }
    public void setCpfIndice(String cpfIndice) { this.cpfIndice = cpfIndice; }

    public String getNisCifrado() { return nisCifrado; }
    public void setNisCifrado(String nisCifrado) { this.nisCifrado = nisCifrado; }

    public StatusCadunico getStatusCadunico() { return statusCadunico; }
    public void setStatusCadunico(StatusCadunico statusCadunico) { this.statusCadunico = statusCadunico; }

    public boolean isConsentimentoLgpd() { return consentimentoLgpd; }
    public void setConsentimentoLgpd(boolean consentimentoLgpd) { this.consentimentoLgpd = consentimentoLgpd; }

    public Instant getConsentimentoEm() { return consentimentoEm; }
    public void setConsentimentoEm(Instant consentimentoEm) { this.consentimentoEm = consentimentoEm; }

    public Instant getCriadoEm() { return criadoEm; }
}
