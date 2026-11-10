package br.recife.bancodealimentos.repositorio;

import br.recife.bancodealimentos.dominio.Pedido;
import br.recife.bancodealimentos.dominio.StatusPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    boolean existsByCodigoAtendimento(String codigoAtendimento);

    Optional<Pedido> findByCodigoAtendimento(String codigoAtendimento);

    @Override
    @EntityGraph(attributePaths = "solicitante")
    Page<Pedido> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "solicitante")
    Page<Pedido> findByStatus(StatusPedido status, Pageable pageable);

    @Query("select p from Pedido p join fetch p.solicitante where p.id = :id")
    Optional<Pedido> buscarComSolicitante(@Param("id") Long id);

    @Query("""
            select count(p) > 0 from Pedido p
            where p.solicitante.cpfIndice = :indice and p.status in :abertos
            """)
    boolean existeAbertoPorCpf(@Param("indice") String indice,
                               @Param("abertos") Collection<StatusPedido> abertos);

    @Query("""
            select count(p) > 0 from Pedido p
            where p.solicitante.nomeNormalizado = :nome
              and p.solicitante.bairroNormalizado = :bairro
              and p.status in :abertos
            """)
    boolean existeAbertoPorNomeEBairro(@Param("nome") String nome,
                                       @Param("bairro") String bairro,
                                       @Param("abertos") Collection<StatusPedido> abertos);
}
