package br.recife.bancodealimentos.servico;

import br.recife.bancodealimentos.dominio.Auditoria;
import br.recife.bancodealimentos.repositorio.AuditoriaRepository;
import br.recife.bancodealimentos.seguranca.UsuarioAutenticado;
import br.recife.bancodealimentos.web.dto.AuditoriaResponse;
import br.recife.bancodealimentos.web.dto.PaginaResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditoriaService {

    private final AuditoriaRepository repositorio;

    public AuditoriaService(AuditoriaRepository repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Participa da transação de quem chamou: se o registro de auditoria falhar,
     * a operação inteira é desfeita (não existe ação sem rastro).
     */
    public void registrar(Long usuarioId, String acao, String tabelaAfetada, Long registroId) {
        repositorio.save(new Auditoria(usuarioId, acao, tabelaAfetada, registroId));
    }

    @Transactional
    public PaginaResponse<AuditoriaResponse> listar(UsuarioAutenticado ator, int pagina, int tamanho) {
        registrar(ator.id(), "LISTAR_AUDITORIA", "auditoria", null);
        var page = repositorio.findAllByOrderByOcorridoEmDescIdDesc(
                PageRequest.of(Math.max(0, pagina), Math.min(Math.max(1, tamanho), 100)));
        return PaginaResponse.de(page.map(a -> new AuditoriaResponse(
                a.getId(), a.getUsuarioId(), a.getAcao(), a.getTabelaAfetada(), a.getRegistroId(), a.getOcorridoEm())));
    }
}
