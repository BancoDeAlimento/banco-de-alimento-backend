package br.recife.bancodealimentos.repositorio;

import br.recife.bancodealimentos.dominio.Doacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoacaoRepository extends JpaRepository<Doacao, Long> {
}