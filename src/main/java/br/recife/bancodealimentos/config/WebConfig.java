package br.recife.bancodealimentos.config;

import br.recife.bancodealimentos.seguranca.AutenticacaoInterceptor;
import br.recife.bancodealimentos.seguranca.LimiteRequisicoesInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final LimiteRequisicoesInterceptor limite;
    private final AutenticacaoInterceptor autenticacao;

    public WebConfig(LimiteRequisicoesInterceptor limite, AutenticacaoInterceptor autenticacao) {
        this.limite = limite;
        this.autenticacao = autenticacao;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(limite)
                .addPathPatterns("/pedidos", "/pedidos/**", "/equipe/**")
                .order(0);
        registry.addInterceptor(autenticacao)
                .addPathPatterns("/equipe/**")
                .order(1);
    }
}
