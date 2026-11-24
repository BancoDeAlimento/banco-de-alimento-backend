
package br.recife.bancodealimentos.config;

import br.recife.bancodealimentos.seguranca.AutenticacaoInterceptor;
import br.recife.bancodealimentos.seguranca.LimiteRequisicoesInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final LimiteRequisicoesInterceptor limite;
    private final AutenticacaoInterceptor autenticacao;
    private final String origemFrontend;

    public WebConfig(
            LimiteRequisicoesInterceptor limite,
            AutenticacaoInterceptor autenticacao,
            @Value("${app.cors.allowed-origin:http://localhost:4200}")
            String origemFrontend) {

        this.limite = limite;
        this.autenticacao = autenticacao;
        this.origemFrontend = origemFrontend;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(limite)
                .addPathPatterns(
                        "/pedidos",
                        "/pedidos/**",
                        "/doacoes",
                        "/doacoes/**",
                        "/entidades",
                        "/entidades/**",
                        "/equipe/**"
                )
                .order(0);

        registry.addInterceptor(autenticacao)
                .addPathPatterns("/equipe/**")
                .order(1);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        configurarCors(registry, "/pedidos", "POST");
        configurarCors(registry, "/pedidos/**", "GET");
        configurarCors(registry, "/doacoes", "POST");
        configurarCors(registry, "/entidades", "POST");
        configurarCors(registry, "/api/impacto", "GET");
    }

    private void configurarCors(
            CorsRegistry registry,
            String caminho,
            String metodo) {

        registry.addMapping(caminho)
                .allowedOrigins(origemFrontend)
                .allowedMethods(metodo, "OPTIONS")
                .allowedHeaders("Content-Type")
                .maxAge(1800);
    }
}
