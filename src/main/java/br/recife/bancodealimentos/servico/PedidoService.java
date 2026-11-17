package br.recife.bancodealimentos.servico;

import br.recife.bancodealimentos.dominio.Origem;
import br.recife.bancodealimentos.dominio.Pedido;
import br.recife.bancodealimentos.dominio.Perfil;
import br.recife.bancodealimentos.dominio.Solicitante;
import br.recife.bancodealimentos.dominio.StatusCadunico;
import br.recife.bancodealimentos.dominio.StatusPedido;
import br.recife.bancodealimentos.excecao.ConflitoException;
import br.recife.bancodealimentos.excecao.NaoEncontradoException;
import br.recife.bancodealimentos.excecao.ProibidoException;
import br.recife.bancodealimentos.excecao.RequisicaoInvalidaException;
import br.recife.bancodealimentos.repositorio.PedidoRepository;
import br.recife.bancodealimentos.repositorio.SolicitanteRepository;
import br.recife.bancodealimentos.seguranca.CriptografiaService;
import br.recife.bancodealimentos.seguranca.UsuarioAutenticado;
import br.recife.bancodealimentos.util.TextoUtil;
import br.recife.bancodealimentos.web.dto.AtualizarPedidoRequest;
import br.recife.bancodealimentos.web.dto.PaginaResponse;
import br.recife.bancodealimentos.web.dto.PedidoCriadoResponse;
import br.recife.bancodealimentos.web.dto.PedidoEquipeResponse;
import br.recife.bancodealimentos.web.dto.PedidoRequest;
import br.recife.bancodealimentos.web.dto.PedidoStatusResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class PedidoService {

    private static final List<StatusPedido> ABERTOS = List.of(
            StatusPedido.RECEBIDO, StatusPedido.EM_ANALISE, StatusPedido.AGUARDANDO_RETIRADA);
    private static final int TENTATIVAS_CODIGO = 10;

    private final PedidoRepository pedidos;
    private final SolicitanteRepository solicitantes;
    private final CriptografiaService criptografia;
    private final CodigoAtendimentoGenerator geradorDeCodigo;
    private final AuditoriaService auditoria;

    public PedidoService(PedidoRepository pedidos,
                         SolicitanteRepository solicitantes,
                         CriptografiaService criptografia,
                         CodigoAtendimentoGenerator geradorDeCodigo,
                         AuditoriaService auditoria) {
        this.pedidos = pedidos;
        this.solicitantes = solicitantes;
        this.criptografia = criptografia;
        this.geradorDeCodigo = geradorDeCodigo;
        this.auditoria = auditoria;
    }

    // ---------------------------------------------------------------- público

    /**
     * Cria solicitante + pedido. Nunca recusa por falta de documento ou por duplicidade:
     * duplicidade apenas marca o pedido para revisão da equipe.
     *
     * @param preenchedor usuário logado (atendimento assistido) ou null quando é a própria pessoa
     */
    @Transactional
    public PedidoCriadoResponse criar(PedidoRequest req, UsuarioAutenticado preenchedor) {
        if (preenchedor != null && preenchedor.perfil() == Perfil.ESTOQUE) {
            throw new ProibidoException();
        }

        String cpf = documentoOpcional(req.cpf(), "CPF");
        String nis = documentoOpcional(req.nis(), "NIS");
        String contato = TextoUtil.vazioParaNulo(req.contato());
        if ((contato == null) != (req.tipoContato() == null)) {
            throw new RequisicaoInvalidaException(
                    "Informe o contato junto com o tipo de contato, ou deixe os dois em branco.");
        }

        String nome = req.nomeOuApelido().strip();
        String bairro = req.bairroOuReferencia().strip();
        String nomeNormalizado = TextoUtil.normalizar(nome);
        String bairroNormalizado = TextoUtil.normalizar(bairro);
        String cpfIndice = cpf != null ? criptografia.indiceCego(cpf) : null;

        // Consulta antes de gravar, para o pedido atual não "achar" a si mesmo.
        String motivoDuplicidade = detectarDuplicidade(cpfIndice, nomeNormalizado, bairroNormalizado);

        Solicitante solicitante = new Solicitante();
        solicitante.setNomeOuApelido(nome);
        solicitante.setNomeNormalizado(nomeNormalizado);
        solicitante.setBairroOuReferencia(bairro);
        solicitante.setBairroNormalizado(bairroNormalizado);
        solicitante.setFaixaIdade(req.faixaIdade());
        solicitante.setContato(contato);
        solicitante.setTipoContato(req.tipoContato());
        if (cpf != null) {
            solicitante.setCpfCifrado(criptografia.cifrar(cpf));
            solicitante.setCpfIndice(cpfIndice);
        }
        if (nis != null) {
            solicitante.setNisCifrado(criptografia.cifrar(nis));
        }
        solicitante.setStatusCadunico(mapearCadunico(req));
        solicitante.setConsentimentoLgpd(true);
        solicitante.setConsentimentoEm(Instant.now());
        solicitantes.save(solicitante);

        Pedido pedido = new Pedido();
        pedido.setSolicitante(solicitante);
        pedido.setNumPessoas(req.numPessoas());
        pedido.setCodigoAtendimento(gerarCodigoUnico());
        pedido.setStatus(StatusPedido.RECEBIDO);
        pedido.setPossivelDuplicidade(motivoDuplicidade != null);
        pedido.setMotivoDuplicidade(motivoDuplicidade);

        String informadoPeloServico = TextoUtil.vazioParaNulo(req.preenchidoPor());
        if (preenchedor != null) {
            pedido.setOrigem(Origem.ASSISTIDO);
            pedido.setPreenchidoPor(preenchedor.nome());
        } else if (informadoPeloServico != null) {
            pedido.setOrigem(Origem.ASSISTIDO);
            pedido.setPreenchidoPor(informadoPeloServico);
        } else {
            pedido.setOrigem(Origem.SOZINHO);
        }
        pedidos.save(pedido);

        auditoria.registrar(preenchedor != null ? preenchedor.id() : null, "CRIAR_PEDIDO", "pedido", pedido.getId());

        String codigo = pedido.getCodigoAtendimento();
        return new PedidoCriadoResponse(
                codigo,
                CodigoAtendimentoGenerator.formatarParaExibicao(codigo),
                pedido.getStatus(),
                "Pedido recebido. Guarde este código para acompanhar o andamento.");
    }

    /** Resposta pública: só o andamento. Código inválido e código inexistente respondem igual. */
    @Transactional(readOnly = true)
    public PedidoStatusResponse consultarStatus(String codigoInformado) {
        String codigo = CodigoAtendimentoGenerator.normalizar(codigoInformado);
        if (!CodigoAtendimentoGenerator.formatoValido(codigo)) {
            throw new NaoEncontradoException("Pedido não encontrado.");
        }
        Pedido pedido = pedidos.findByCodigoAtendimento(codigo)
                .orElseThrow(() -> new NaoEncontradoException("Pedido não encontrado."));
        String ponto = pedido.getStatus() == StatusPedido.AGUARDANDO_RETIRADA ? pedido.getPontoRetirada() : null;
        return new PedidoStatusResponse(pedido.getCodigoAtendimento(), pedido.getStatus(), ponto, pedido.getAtualizadoEm());
    }

    // ----------------------------------------------------------------- equipe

    @Transactional
    public PaginaResponse<PedidoEquipeResponse> listar(StatusPedido status, int pagina, int tamanho,
                                                       UsuarioAutenticado ator) {
        Pageable pageable = PageRequest.of(
                Math.max(0, pagina),
                Math.min(Math.max(1, tamanho), 100),
                Sort.by(Sort.Direction.DESC, "criadoEm"));
        Page<Pedido> page = status == null ? pedidos.findAll(pageable) : pedidos.findByStatus(status, pageable);
        auditoria.registrar(ator.id(), "LISTAR_PEDIDOS", "pedido", null);
        return PaginaResponse.de(page.map(this::paraResposta));
    }

    @Transactional
    public PedidoEquipeResponse detalhar(Long id, UsuarioAutenticado ator) {
        Pedido pedido = buscar(id);
        auditoria.registrar(ator.id(), "VISUALIZAR_PEDIDO", "pedido", id);
        return paraResposta(pedido);
    }

    @Transactional
    public PedidoEquipeResponse atualizar(Long id, AtualizarPedidoRequest req, UsuarioAutenticado ator) {
        if (req.status() == null && req.pontoRetirada() == null) {
            throw new RequisicaoInvalidaException("Informe o novo status e/ou o ponto de retirada.");
        }
        Pedido pedido = buscar(id);
        if (pedido.getStatus().terminal()) {
            throw new ConflitoException("O pedido já está " + pedido.getStatus() + " e não pode mais ser alterado.");
        }
        if (req.pontoRetirada() != null) {
            pedido.setPontoRetirada(TextoUtil.vazioParaNulo(req.pontoRetirada()));
        }
        if (req.status() != null && req.status() != pedido.getStatus()) {
            if (!pedido.getStatus().podeIrPara(req.status())) {
                throw new ConflitoException(
                        "Transição não permitida: " + pedido.getStatus() + " -> " + req.status() + ".");
            }
            pedido.setStatus(req.status());
        }
        if (pedido.getStatus() == StatusPedido.AGUARDANDO_RETIRADA && pedido.getPontoRetirada() == null) {
            throw new RequisicaoInvalidaException(
                    "Defina o ponto de retirada antes de marcar o pedido como aguardando retirada.");
        }
        auditoria.registrar(ator.id(), "ATUALIZAR_PEDIDO", "pedido", id);
        return paraResposta(pedido);
    }

    /** Registra que um gestor autorizado confirmou o CadÚnico. O sistema não consulta a base oficial. */
    @Transactional
    public PedidoEquipeResponse verificarCadunico(Long id, UsuarioAutenticado ator) {
        Pedido pedido = buscar(id);
        pedido.getSolicitante().setStatusCadunico(StatusCadunico.VERIFICADO);
        auditoria.registrar(ator.id(), "VERIFICAR_CADUNICO", "solicitante", pedido.getSolicitante().getId());
        return paraResposta(pedido);
    }

    // ---------------------------------------------------------------- privado

    private Pedido buscar(Long id) {
        return pedidos.buscarComSolicitante(id)
                .orElseThrow(() -> new NaoEncontradoException("Pedido não encontrado."));
    }

    /** Aceita pontuação (123.456.789-09), guarda só dígitos. Vazio = não informado. */
    private static String documentoOpcional(String informado, String rotulo) {
        String valor = TextoUtil.vazioParaNulo(informado);
        if (valor == null) {
            return null;
        }
        String digitos = TextoUtil.soDigitos(valor);
        if (digitos.length() != 11) {
            throw new RequisicaoInvalidaException(
                    rotulo + " inválido: deve ter 11 dígitos. Se a pessoa não tiver, deixe em branco.");
        }
        return digitos;
    }

    private static StatusCadunico mapearCadunico(PedidoRequest req) {
        if (req.cadunico() == null) {
            return null;
        }
        return switch (req.cadunico()) {
            case SIM -> StatusCadunico.INFORMADO;
            case NAO -> StatusCadunico.NAO_ESTA;
            case NAO_SEI -> StatusCadunico.NAO_SEI;
        };
    }

    private String detectarDuplicidade(String cpfIndice, String nomeNormalizado, String bairroNormalizado) {
        if (cpfIndice != null && pedidos.existeAbertoPorCpf(cpfIndice, ABERTOS)) {
            return "Já existe pedido em aberto com o mesmo CPF.";
        }
        if (pedidos.existeAbertoPorNomeEBairro(nomeNormalizado, bairroNormalizado, ABERTOS)) {
            return "Já existe pedido em aberto com o mesmo nome e bairro/referência.";
        }
        return null;
    }

    private String gerarCodigoUnico() {
        for (int i = 0; i < TENTATIVAS_CODIGO; i++) {
            String codigo = geradorDeCodigo.gerar();
            if (!pedidos.existsByCodigoAtendimento(codigo)) {
                return codigo;
            }
        }
        throw new IllegalStateException("Não foi possível gerar um código de atendimento único.");
    }

    private PedidoEquipeResponse paraResposta(Pedido p) {
        Solicitante s = p.getSolicitante();
        return new PedidoEquipeResponse(
                p.getId(),
                p.getCodigoAtendimento(),
                p.getStatus(),
                p.getNumPessoas(),
                p.getPontoRetirada(),
                p.getOrigem(),
                p.getPreenchidoPor(),
                p.isPossivelDuplicidade(),
                p.getMotivoDuplicidade(),
                p.getCriadoEm(),
                p.getAtualizadoEm(),
                new PedidoEquipeResponse.SolicitanteEquipe(
                        s.getNomeOuApelido(),
                        s.getFaixaIdade(),
                        s.getBairroOuReferencia(),
                        s.getContato(),
                        s.getTipoContato(),
                        s.getCpfCifrado() != null,
                        s.getNisCifrado() != null,
                        s.getStatusCadunico()));
    }
}
