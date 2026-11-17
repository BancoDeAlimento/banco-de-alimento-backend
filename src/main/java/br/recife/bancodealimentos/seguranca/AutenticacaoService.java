package br.recife.bancodealimentos.seguranca;

import br.recife.bancodealimentos.dominio.UsuarioEquipe;
import br.recife.bancodealimentos.excecao.NaoAutenticadoException;
import br.recife.bancodealimentos.repositorio.UsuarioEquipeRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

/**
 * Autenticação HTTP Basic (email:senha). Use sempre atrás de HTTPS.
 */
@Service
public class AutenticacaoService {

    private final UsuarioEquipeRepository usuarios;
    private final PasswordEncoder encoder;
    /** Usado para gastar o mesmo tempo quando o email não existe (evita enumerar usuários por tempo de resposta). */
    private final String hashFicticio;

    public AutenticacaoService(UsuarioEquipeRepository usuarios, PasswordEncoder encoder) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.hashFicticio = encoder.encode("senha-ficticia-para-igualar-tempo");
    }

    /**
     * Sem cabeçalho Authorization: retorna vazio.
     * Com cabeçalho inválido, usuário inativo ou senha errada: lança NaoAutenticadoException.
     */
    public Optional<UsuarioAutenticado> autenticar(HttpServletRequest request) {
        String cabecalho = request.getHeader("Authorization");
        if (cabecalho == null || cabecalho.isBlank()) {
            return Optional.empty();
        }
        if (!cabecalho.regionMatches(true, 0, "Basic ", 0, 6)) {
            throw new NaoAutenticadoException();
        }
        String decodificado;
        try {
            byte[] bytes = Base64.getDecoder().decode(cabecalho.substring(6).strip());
            decodificado = new String(bytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new NaoAutenticadoException();
        }
        int separador = decodificado.indexOf(':');
        if (separador <= 0) {
            throw new NaoAutenticadoException();
        }
        String email = decodificado.substring(0, separador);
        String senha = decodificado.substring(separador + 1);

        UsuarioEquipe usuario = usuarios.findByEmailIgnoreCase(email.strip()).orElse(null);
        boolean senhaConfere = encoder.matches(senha, usuario != null ? usuario.getSenhaHash() : hashFicticio);
        if (usuario == null || !senhaConfere || !usuario.isAtivo()) {
            throw new NaoAutenticadoException();
        }
        return Optional.of(new UsuarioAutenticado(usuario.getId(), usuario.getNome(), usuario.getPerfil()));
    }

    public String gerarHash(String senha) {
        return encoder.encode(senha);
    }
}
