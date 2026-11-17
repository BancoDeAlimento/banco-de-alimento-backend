package br.recife.bancodealimentos.servico;

import br.recife.bancodealimentos.config.AppProperties;
import br.recife.bancodealimentos.dominio.Perfil;
import br.recife.bancodealimentos.dominio.UsuarioEquipe;
import br.recife.bancodealimentos.repositorio.UsuarioEquipeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * Cria o primeiro administrador quando o banco está vazio.
 * Defina app.bootstrap.admin-email / admin-senha (ou as variáveis APP_BOOTSTRAP_ADMINEMAIL / APP_BOOTSTRAP_ADMINSENHA).
 * Sem senha configurada, gera uma aleatória e a mostra UMA vez no log: troque-a em seguida.
 */
@Component
public class AdminInicial implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInicial.class);
    private static final String ALFABETO = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";

    private final UsuarioEquipeRepository usuarios;
    private final PasswordEncoder encoder;
    private final AppProperties.Bootstrap config;

    public AdminInicial(UsuarioEquipeRepository usuarios, PasswordEncoder encoder, AppProperties props) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.config = props.bootstrap();
    }

    @Override
    public void run(ApplicationArguments args) {
        if (usuarios.count() > 0) {
            return;
        }
        String email = config.adminEmail() == null || config.adminEmail().isBlank()
                ? "admin@bancodealimentos.local"
                : config.adminEmail().strip();
        boolean gerada = config.adminSenha() == null || config.adminSenha().isBlank();
        String senha = gerada ? gerarSenha() : config.adminSenha();

        usuarios.save(new UsuarioEquipe("Administrador", email, encoder.encode(senha), Perfil.ADMIN));

        if (gerada) {
            log.warn("Administrador inicial criado. E-mail: {} | Senha gerada (aparece só agora): {}", email, senha);
        } else {
            log.info("Administrador inicial criado com o e-mail {}.", email);
        }
    }

    private static String gerarSenha() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(20);
        for (int i = 0; i < 20; i++) {
            sb.append(ALFABETO.charAt(random.nextInt(ALFABETO.length())));
        }
        return sb.toString();
    }
}
