package br.recife.bancodealimentos.repositorio;

import br.recife.bancodealimentos.dominio.UsuarioEquipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioEquipeRepository extends JpaRepository<UsuarioEquipe, Long> {

    Optional<UsuarioEquipe> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
