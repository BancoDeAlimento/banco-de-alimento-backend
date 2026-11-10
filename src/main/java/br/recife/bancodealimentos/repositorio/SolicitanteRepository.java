package br.recife.bancodealimentos.repositorio;

import br.recife.bancodealimentos.dominio.Solicitante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitanteRepository extends JpaRepository<Solicitante, Long> {
}
