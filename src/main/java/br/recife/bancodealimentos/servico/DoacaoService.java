package br.recife.bancodealimentos.servico;

import br.recife.bancodealimentos.dominio.Doacao;
import br.recife.bancodealimentos.repositorio.DoacaoRepository;
import br.recife.bancodealimentos.util.TextoUtil;
import br.recife.bancodealimentos.web.dto.DoacaoCriadaResponse;
import br.recife.bancodealimentos.web.dto.DoacaoRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class DoacaoService {

    private final DoacaoRepository doacoes;
    private final AuditoriaService auditoria;

    public DoacaoService(DoacaoRepository doacoes, AuditoriaService auditoria) {
        this.doacoes = doacoes;
        this.auditoria = auditoria;
    }

    @Transactional
    public DoacaoCriadaResponse criar(DoacaoRequest req) {
        Doacao d = new Doacao();
        d.setNomeDoador(req.nomeDoador().strip());
        d.setTipoDoador(req.tipoDoador());
        d.setContato(req.contato().strip());
        d.setDescricaoAlimentos(req.descricaoAlimentos().strip());
        d.setQuantidadeEstimadaKg(req.quantidadeEstimadaKg());
        d.setBairroOuLocal(TextoUtil.vazioParaNulo(req.bairroOuLocal()));
        d.setObservacoes(TextoUtil.vazioParaNulo(req.observacoes()));
        d.setConsentimentoEm(Instant.now());
        doacoes.save(d);

        auditoria.registrar(null, "CRIAR_DOACAO", "doacao", d.getId());
        return new DoacaoCriadaResponse(d.getId(),
                "Doação recebida. Obrigado! A equipe entrará em contato.");
    }
}