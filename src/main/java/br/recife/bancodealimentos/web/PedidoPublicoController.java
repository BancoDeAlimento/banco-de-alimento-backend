package br.recife.bancodealimentos.web;

import br.recife.bancodealimentos.seguranca.AutenticacaoService;
import br.recife.bancodealimentos.seguranca.UsuarioAutenticado;
import br.recife.bancodealimentos.servico.PedidoService;
import br.recife.bancodealimentos.web.dto.PedidoCriadoResponse;
import br.recife.bancodealimentos.web.dto.PedidoRequest;
import br.recife.bancodealimentos.web.dto.PedidoStatusResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/** Endpoints abertos ao público. Login é opcional no POST (atendimento assistido). */
@RestController
@RequestMapping("/pedidos")
public class PedidoPublicoController {

    private final PedidoService pedidos;
    private final AutenticacaoService autenticacao;

    public PedidoPublicoController(PedidoService pedidos, AutenticacaoService autenticacao) {
        this.pedidos = pedidos;
        this.autenticacao = autenticacao;
    }

    @PostMapping
    public ResponseEntity<PedidoCriadoResponse> criar(@Valid @RequestBody PedidoRequest requisicao,
                                                      HttpServletRequest http) {
        UsuarioAutenticado preenchedor = autenticacao.autenticar(http).orElse(null);
        PedidoCriadoResponse resposta = pedidos.criar(requisicao, preenchedor);
        return ResponseEntity.created(URI.create("/pedidos/" + resposta.codigo())).body(resposta);
    }

    @GetMapping("/{codigo}")
    public PedidoStatusResponse consultar(@PathVariable String codigo) {
        return pedidos.consultarStatus(codigo);
    }
}
