package br.recife.bancodealimentos.web;

import br.recife.bancodealimentos.dominio.Perfil;
import br.recife.bancodealimentos.dominio.StatusPedido;
import br.recife.bancodealimentos.seguranca.AutenticacaoInterceptor;
import br.recife.bancodealimentos.seguranca.RequerPerfil;
import br.recife.bancodealimentos.seguranca.UsuarioAutenticado;
import br.recife.bancodealimentos.servico.PedidoService;
import br.recife.bancodealimentos.web.dto.AtualizarPedidoRequest;
import br.recife.bancodealimentos.web.dto.PaginaResponse;
import br.recife.bancodealimentos.web.dto.PedidoEquipeResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/equipe/pedidos")
public class PedidoEquipeController {

    private final PedidoService pedidos;

    public PedidoEquipeController(PedidoService pedidos) {
        this.pedidos = pedidos;
    }

    @GetMapping
    @RequerPerfil({Perfil.ADMIN, Perfil.TRIAGEM})
    public PaginaResponse<PedidoEquipeResponse> listar(
            @RequestParam(required = false) StatusPedido status,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho,
            @RequestAttribute(AutenticacaoInterceptor.ATRIBUTO) UsuarioAutenticado ator) {
        return pedidos.listar(status, pagina, tamanho, ator);
    }

    @GetMapping("/{id}")
    @RequerPerfil({Perfil.ADMIN, Perfil.TRIAGEM})
    public PedidoEquipeResponse detalhar(
            @PathVariable Long id,
            @RequestAttribute(AutenticacaoInterceptor.ATRIBUTO) UsuarioAutenticado ator) {
        return pedidos.detalhar(id, ator);
    }

    @PatchMapping("/{id}")
    @RequerPerfil({Perfil.ADMIN, Perfil.TRIAGEM})
    public PedidoEquipeResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarPedidoRequest requisicao,
            @RequestAttribute(AutenticacaoInterceptor.ATRIBUTO) UsuarioAutenticado ator) {
        return pedidos.atualizar(id, requisicao, ator);
    }

    /** Só gestor (ADMIN) confirma o CadÚnico, conforme a regra de sigilo. */
    @PostMapping("/{id}/cadunico/verificar")
    @RequerPerfil({Perfil.ADMIN})
    public PedidoEquipeResponse verificarCadunico(
            @PathVariable Long id,
            @RequestAttribute(AutenticacaoInterceptor.ATRIBUTO) UsuarioAutenticado ator) {
        return pedidos.verificarCadunico(id, ator);
    }
}
