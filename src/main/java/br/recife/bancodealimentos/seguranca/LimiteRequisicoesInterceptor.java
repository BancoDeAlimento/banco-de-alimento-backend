package br.recife.bancodealimentos.seguranca;

import br.recife.bancodealimentos.config.AppProperties;
import br.recife.bancodealimentos.excecao.LimiteExcedidoException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Limita requisições por IP. Protege principalmente GET /pedidos/{codigo}, que é público,
 * contra tentativas de adivinhar códigos.
 *
 * Atrás de proxy/balanceador, configure server.forward-headers-strategy para que o IP real
 * chegue aqui (senão todos aparecem com o IP do proxy).
 */
@Component
public class LimiteRequisicoesInterceptor implements HandlerInterceptor {

    private final LimitadorRequisicoes limitador;
    private final AppProperties.RateLimit limites;

    public LimiteRequisicoesInterceptor(LimitadorRequisicoes limitador, AppProperties props) {
        this.limitador = limitador;
        this.limites = props.rateLimit();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String caminho = request.getRequestURI();
        String balde;
        int limite;
        if (caminho.startsWith("/equipe")) {
            balde = "equipe";
            limite = limites.equipePorMinuto();
        } else if ("POST".equalsIgnoreCase(request.getMethod())) {
            balde = "criacao";
            limite = limites.criacaoPorMinuto();
        } else {
            balde = "consulta";
            limite = limites.consultaPorMinuto();
        }
        if (!limitador.permitir(balde + ":" + request.getRemoteAddr(), limite)) {
            throw new LimiteExcedidoException();
        }
        return true;
    }
}
