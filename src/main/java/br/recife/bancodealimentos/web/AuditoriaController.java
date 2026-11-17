package br.recife.bancodealimentos.web;

import br.recife.bancodealimentos.dominio.Perfil;
import br.recife.bancodealimentos.seguranca.AutenticacaoInterceptor;
import br.recife.bancodealimentos.seguranca.RequerPerfil;
import br.recife.bancodealimentos.seguranca.UsuarioAutenticado;
import br.recife.bancodealimentos.servico.AuditoriaService;
import br.recife.bancodealimentos.web.dto.AuditoriaResponse;
import br.recife.bancodealimentos.web.dto.PaginaResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/equipe/auditoria")
@RequerPerfil({Perfil.ADMIN})
public class AuditoriaController {

    private final AuditoriaService auditoria;

    public AuditoriaController(AuditoriaService auditoria) {
        this.auditoria = auditoria;
    }

    @GetMapping
    public PaginaResponse<AuditoriaResponse> listar(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "50") int tamanho,
            @RequestAttribute(AutenticacaoInterceptor.ATRIBUTO) UsuarioAutenticado ator) {
        return auditoria.listar(ator, pagina, tamanho);
    }
}
