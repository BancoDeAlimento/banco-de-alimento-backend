package br.recife.bancodealimentos.seguranca;

import br.recife.bancodealimentos.dominio.Perfil;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Perfis autorizados a chamar o endpoint. Em /equipe/**, endpoint sem esta anotação é negado (403).
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RequerPerfil {
    Perfil[] value();
}
