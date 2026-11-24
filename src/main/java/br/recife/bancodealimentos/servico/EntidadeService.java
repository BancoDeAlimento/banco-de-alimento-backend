package br.recife.bancodealimentos.servico;

import br.recife.bancodealimentos.dominio.Entidade;
import br.recife.bancodealimentos.dominio.StatusCredenciamento;
import br.recife.bancodealimentos.excecao.RequisicaoInvalidaException;
import br.recife.bancodealimentos.repositorio.EntidadeRepository;
import br.recife.bancodealimentos.seguranca.CriptografiaService;
import br.recife.bancodealimentos.util.DocumentoUtil;
import br.recife.bancodealimentos.util.TextoUtil;
import br.recife.bancodealimentos.web.dto.EntidadeCriadaResponse;
import br.recife.bancodealimentos.web.dto.EntidadeRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class EntidadeService {

    private final EntidadeRepository entidades;
    private final CriptografiaService criptografia;
    private final AuditoriaService auditoria;

    public EntidadeService(EntidadeRepository entidades,
                           CriptografiaService criptografia,
                           AuditoriaService auditoria) {
        this.entidades = entidades;
        this.criptografia = criptografia;
        this.auditoria = auditoria;
    }

    /**
     * Cria o pré-cadastro. Documento inválido é recusado (erro de digitação),
     * mas duplicidade nunca recusa: só marca para revisão da equipe.
     */
    @Transactional
    public EntidadeCriadaResponse criar(EntidadeRequest req) {
        String documento = DocumentoUtil.normalizar(req.documento());
        if (!DocumentoUtil.cnpjValido(documento) && !DocumentoUtil.cpfValido(documento)) {
            throw new RequisicaoInvalidaException("Documento inválido: confira o CNPJ ou CPF informado.");
        }

        String indice = criptografia.indiceCego(documento);
        boolean duplicado = entidades.existsByDocumentoIndice(indice);

        Entidade e = new Entidade();
        e.setNome(req.nome().strip());
        e.setTipo(req.tipo());
        e.setDocumentoCifrado(criptografia.cifrar(documento));
        e.setDocumentoIndice(indice);
        e.setContato(req.contato().strip());
        e.setBairro(req.bairro().strip());
        e.setDescricaoAtividades(TextoUtil.vazioParaNulo(req.descricaoAtividades()));
        e.setStatus(StatusCredenciamento.PENDENTE);
        e.setPossivelDuplicidade(duplicado);
        e.setMotivoDuplicidade(duplicado ? "Já existe entidade cadastrada com o mesmo documento." : null);
        e.setConsentimentoEm(Instant.now());
        entidades.save(e);

        auditoria.registrar(null, "CRIAR_ENTIDADE", "entidade", e.getId());
        // Mesma resposta com ou sem duplicidade: não revelar quem já está cadastrado.
        return new EntidadeCriadaResponse(e.getId(),
                "Cadastro recebido. A equipe da Secretaria vai analisar e entrar em contato.");
    }
}