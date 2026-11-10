package br.recife.bancodealimentos.servico;

import br.recife.bancodealimentos.dominio.UsuarioEquipe;
import br.recife.bancodealimentos.excecao.ConflitoException;
import br.recife.bancodealimentos.excecao.NaoEncontradoException;
import br.recife.bancodealimentos.repositorio.UsuarioEquipeRepository;
import br.recife.bancodealimentos.seguranca.AutenticacaoService;
import br.recife.bancodealimentos.seguranca.UsuarioAutenticado;
import br.recife.bancodealimentos.web.dto.CriarUsuarioRequest;
import br.recife.bancodealimentos.web.dto.UsuarioResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioEquipeRepository usuarios;
    private final AutenticacaoService autenticacao;
    private final AuditoriaService auditoria;

    public UsuarioService(UsuarioEquipeRepository usuarios, AutenticacaoService autenticacao, AuditoriaService auditoria) {
        this.usuarios = usuarios;
        this.autenticacao = autenticacao;
        this.auditoria = auditoria;
    }

    @Transactional
    public UsuarioResponse criar(CriarUsuarioRequest req, UsuarioAutenticado ator) {
        if (usuarios.existsByEmailIgnoreCase(req.email().strip())) {
            throw new ConflitoException("Já existe um usuário com este e-mail.");
        }
        UsuarioEquipe usuario = new UsuarioEquipe(
                req.nome().strip(), req.email(), autenticacao.gerarHash(req.senha()), req.perfil());
        usuarios.save(usuario);
        auditoria.registrar(ator.id(), "CRIAR_USUARIO", "usuario_equipe", usuario.getId());
        return paraResposta(usuario);
    }

    @Transactional
    public List<UsuarioResponse> listar(UsuarioAutenticado ator) {
        auditoria.registrar(ator.id(), "LISTAR_USUARIOS", "usuario_equipe", null);
        return usuarios.findAll().stream().map(this::paraResposta).toList();
    }

    @Transactional
    public UsuarioResponse alterarAtivo(Long id, boolean ativo, UsuarioAutenticado ator) {
        UsuarioEquipe usuario = usuarios.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Usuário não encontrado."));
        if (!ativo && usuario.getId().equals(ator.id())) {
            throw new ConflitoException("Você não pode desativar o próprio usuário.");
        }
        usuario.setAtivo(ativo);
        auditoria.registrar(ator.id(), ativo ? "ATIVAR_USUARIO" : "DESATIVAR_USUARIO", "usuario_equipe", id);
        return paraResposta(usuario);
    }

    private UsuarioResponse paraResposta(UsuarioEquipe u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getPerfil(), u.isAtivo(), u.getCriadoEm());
    }
}
