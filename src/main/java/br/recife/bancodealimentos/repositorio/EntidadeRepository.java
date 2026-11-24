package br.recife.bancodealimentos.repositorio;

import br.recife.bancodealimentos.dominio.Entidade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntidadeRepository extends JpaRepository<Entidade, Long> {

    boolean existsByDocumentoIndice(String documentoIndice);
}