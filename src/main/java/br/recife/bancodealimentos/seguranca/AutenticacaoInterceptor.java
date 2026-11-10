package br.recife.bancodealimentos.seguranca;

import br.recife.bancodealimentos.excecao.NaoAutenticadoException;
import br.recife.bancodealimentos.excecao.ProibidoException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

/**
 * Protege /equipe/**: exige login e confere o perfil declarado em @RequerPerfil.
 * Sem anotação, nega o acesso (seguro por padrão).
 */
@Component
public class AutenticacaoInterceptor implements HandlerInterceptor {

    public static final String ATRIBUTO = "usuarioAutenticado";

    private final AutenticacaoService autenticacao;

    public AutenticacaoInterceptor(AutenticacaoService autenticacao) {
        this.autenticacao = autenticacao;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod metodo)) {
            return true;
        }
        UsuarioAutenticado usuario = autenticacao.autenticar(request).orElseThrow(NaoAutenticadoException::new);

        RequerPerfil exigencia = metodo.getMethodAnnotation(RequerPerfil.class);
        if (exigencia == null) {
            exigencia = metodo.getBeanType().getAnnotation(RequerPerfil.class);
        }
        if (exigencia == null || !Arrays.asList(exigencia.value()).contains(usuario.perfil())) {
            throw new ProibidoException();
        }
        request.setAttribute(ATRIBUTO, usuario);
        return true;
    }
}
