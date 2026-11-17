package br.recife.bancodealimentos.web;

import br.recife.bancodealimentos.dominio.Perfil;
import br.recife.bancodealimentos.seguranca.AutenticacaoInterceptor;
import br.recife.bancodealimentos.seguranca.RequerPerfil;
import br.recife.bancodealimentos.seguranca.UsuarioAutenticado;
import br.recife.bancodealimentos.servico.UsuarioService;
import br.recife.bancodealimentos.web.dto.AlterarAtivoRequest;
import br.recife.bancodealimentos.web.dto.CriarUsuarioRequest;
import br.recife.bancodealimentos.web.dto.UsuarioResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/equipe/usuarios")
@RequerPerfil({Perfil.ADMIN})
public class UsuarioEquipeController {

    private final UsuarioService usuarios;

    public UsuarioEquipeController(UsuarioService usuarios) {
        this.usuarios = usuarios;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse criar(
            @Valid @RequestBody CriarUsuarioRequest requisicao,
            @RequestAttribute(AutenticacaoInterceptor.ATRIBUTO) UsuarioAutenticado ator) {
        return usuarios.criar(requisicao, ator);
    }

    @GetMapping
    public List<UsuarioResponse> listar(
            @RequestAttribute(AutenticacaoInterceptor.ATRIBUTO) UsuarioAutenticado ator) {
        return usuarios.listar(ator);
    }

    @PatchMapping("/{id}/ativo")
    public UsuarioResponse alterarAtivo(
            @PathVariable Long id,
            @Valid @RequestBody AlterarAtivoRequest requisicao,
            @RequestAttribute(AutenticacaoInterceptor.ATRIBUTO) UsuarioAutenticado ator) {
        return usuarios.alterarAtivo(id, requisicao.ativo(), ator);
    }
}
