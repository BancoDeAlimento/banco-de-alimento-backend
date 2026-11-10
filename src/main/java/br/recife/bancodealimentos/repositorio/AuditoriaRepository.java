package br.recife.bancodealimentos.repositorio;

import br.recife.bancodealimentos.dominio.Auditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    Page<Auditoria> findAllByOrderByOcorridoEmDescIdDesc(Pageable pageable);
}
