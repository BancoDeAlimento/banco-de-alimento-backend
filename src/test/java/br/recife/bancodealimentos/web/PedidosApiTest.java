package br.recife.bancodealimentos.web;

import br.recife.bancodealimentos.dominio.Origem;
import br.recife.bancodealimentos.dominio.Pedido;
import br.recife.bancodealimentos.dominio.Perfil;
import br.recife.bancodealimentos.dominio.Solicitante;
import br.recife.bancodealimentos.dominio.StatusCadunico;
import br.recife.bancodealimentos.dominio.UsuarioEquipe;
import br.recife.bancodealimentos.repositorio.AuditoriaRepository;
import br.recife.bancodealimentos.repositorio.PedidoRepository;
import br.recife.bancodealimentos.repositorio.UsuarioEquipeRepository;
import br.recife.bancodealimentos.seguranca.CriptografiaService;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
class PedidosApiTest {

    private static final String SENHA = "SenhaForte#2026";
    private static final String CPF_COM_PONTUACAO = "529.982.247-25";
    private static final String CPF_DIGITOS = "52998224725";

    private static final String ADMIN = "admin-teste@exemplo.org";
    private static final String TRIAGEM = "triagem-teste@exemplo.org";
    private static final String ESTOQUE = "estoque-teste@exemplo.org";
    private static final String CRAS = "cras-teste@exemplo.org";

    @Autowired private WebApplicationContext contexto;
    @Autowired private UsuarioEquipeRepository usuarios;
    @Autowired private PedidoRepository pedidos;
    @Autowired private AuditoriaRepository auditorias;
    @Autowired private PasswordEncoder encoder;
    @Autowired private CriptografiaService criptografia;

    private MockMvc mvc;

    @BeforeEach
    void preparar() {
        mvc = MockMvcBuilders.webAppContextSetup(contexto).build();
        garantirUsuario(ADMIN, "Admin de Teste", Perfil.ADMIN);
        garantirUsuario(TRIAGEM, "Triagem de Teste", Perfil.TRIAGEM);
        garantirUsuario(ESTOQUE, "Estoque de Teste", Perfil.ESTOQUE);
        garantirUsuario(CRAS, "Ana do CRAS", Perfil.ASSISTENTE_CRAS);
    }

    // ------------------------------------------------------ fluxo da pessoa

    @Test
    void criaPedidoSemNenhumDocumentoEPermiteAcompanharPeloCodigo() throws Exception {
        String corpo = pedidoJson("Zé da Ponte", unico("Ponte da Boa Vista"), "");

        String resposta = mvc.perform(post("/pedidos").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("RECEBIDO"))
                .andExpect(jsonPath("$.codigo").isString())
                .andExpect(jsonPath("$.codigoExibicao").isString())
                .andReturn().getResponse().getContentAsString();

        String codigo = JsonPath.read(resposta, "$.codigo");
        String exibicao = JsonPath.read(resposta, "$.codigoExibicao");
        assertThat(codigo).hasSize(8).doesNotContain("0", "O", "1", "I", "L");
        assertThat(exibicao).isEqualTo(codigo.substring(0, 4) + "-" + codigo.substring(4));

        mvc.perform(get("/pedidos/" + codigo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RECEBIDO"))
                .andExpect(jsonPath("$.nomeOuApelido").doesNotExist())
                .andExpect(jsonPath("$.contato").doesNotExist())
                .andExpect(jsonPath("$.solicitante").doesNotExist());

        // aceita o código como a pessoa costuma digitar: minúsculo e com hífen
        mvc.perform(get("/pedidos/" + exibicao.toLowerCase()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(codigo));
    }

    @Test
    void codigoInexistenteOuMalFormadoRespondeIgual404() throws Exception {
        mvc.perform(get("/pedidos/AAAAAAAA")).andExpect(status().isNotFound());
        mvc.perform(get("/pedidos/abc")).andExpect(status().isNotFound());
        mvc.perform(get("/pedidos/K7QMR3X0")).andExpect(status().isNotFound());
    }

    @Test
    void exigeConsentimentoParaTratarOsDados() throws Exception {
        String corpo = """
                {"nomeOuApelido":"Fulano","bairroOuReferencia":"Ibura","numPessoas":2,"consentimentoLgpd":false}
                """;
        mvc.perform(post("/pedidos").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.consentimentoLgpd").exists());
    }



    @Test
    void camposObrigatoriosSaoApenasOMinimo() throws Exception {

        mvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.nomeOuApelido").exists())
                .andExpect(jsonPath("$.erros.bairroOuReferencia").exists())
                .andExpect(jsonPath("$.erros.numPessoas").exists())
                .andExpect(jsonPath("$.erros.consentimentoLgpd").exists())
                .andExpect(jsonPath("$.erros.cpf").doesNotExist())
                .andExpect(jsonPath("$.erros.nis").doesNotExist())
                .andExpect(jsonPath("$.erros.contato").doesNotExist());
    }



    @Test
    void cpfInformadoMasInvalidoEhRejeitadoComOrientacao() throws Exception {
        mvc.perform(post("/pedidos").contentType(MediaType.APPLICATION_JSON)
                        .content(pedidoJson("Fulano", unico("Ibura"), ",\"cpf\":\"123\"")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", containsString("em branco")));
    }

    @Test
    void contatoPrecisaVirJuntoDoTipo() throws Exception {
        mvc.perform(post("/pedidos").contentType(MediaType.APPLICATION_JSON)
                        .content(pedidoJson("Fulano", unico("Ibura"), ",\"contato\":\"81999990000\"")))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/pedidos").contentType(MediaType.APPLICATION_JSON)
                        .content(pedidoJson("Fulano", unico("Ibura"),
                                ",\"contato\":\"81999990000\",\"tipoContato\":\"WHATSAPP\"")))
                .andExpect(status().isCreated());
    }

    // -------------------------------------------------------- LGPD / dados

    @Test
    void cpfENisSaoGravadosCifradosComIndiceParaDuplicidade() throws Exception {
        String codigo = criarPedido(pedidoJson("Maria", unico("Ibura"),
                ",\"cpf\":\"" + CPF_COM_PONTUACAO + "\",\"nis\":\"12345678901\",\"cadunico\":\"SIM\""), null);

        Pedido pedido = pedidos.findByCodigoAtendimento(codigo).orElseThrow();
        Solicitante s = pedidos.buscarComSolicitante(pedido.getId()).orElseThrow().getSolicitante();

        assertThat(s.getCpfCifrado()).isNotBlank().doesNotContain(CPF_DIGITOS);
        assertThat(s.getNisCifrado()).isNotBlank().doesNotContain("12345678901");
        assertThat(criptografia.decifrar(s.getCpfCifrado())).isEqualTo(CPF_DIGITOS);
        assertThat(s.getCpfIndice()).isEqualTo(criptografia.indiceCego(CPF_DIGITOS));
        assertThat(s.getStatusCadunico()).isEqualTo(StatusCadunico.INFORMADO);
        assertThat(s.isConsentimentoLgpd()).isTrue();
        assertThat(s.getConsentimentoEm()).isNotNull();
    }

    @Test
    void respostasDaEquipeNuncaExpoemCpfNemNis() throws Exception {
        String codigo = criarPedido(pedidoJson("Joana", unico("Ibura"),
                ",\"cpf\":\"" + CPF_COM_PONTUACAO + "\",\"nis\":\"12345678901\""), null);
        Long id = pedidos.findByCodigoAtendimento(codigo).orElseThrow().getId();

        mvc.perform(get("/equipe/pedidos/" + id).header("Authorization", basic(TRIAGEM)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.solicitante.possuiCpf").value(true))
                .andExpect(jsonPath("$.solicitante.possuiNis").value(true))
                .andExpect(content().string(not(containsString(CPF_DIGITOS))))
                .andExpect(content().string(not(containsString(CPF_COM_PONTUACAO))))
                .andExpect(content().string(not(containsString("12345678901"))));
    }

    // --------------------------------------------------------- duplicidade

    @Test
    void duplicidadeGeraAlertaParaARevisaoMasNuncaRecusaOPedido() throws Exception {
        String bairro = unico("Casa Amarela");
        String primeiro = criarPedido(pedidoJson("Maria José", bairro, ""), null);
        String segundo = criarPedido(pedidoJson("  MARIA JOSE ", bairro, ""), null); // aceito (201)

        Pedido p1 = pedidos.findByCodigoAtendimento(primeiro).orElseThrow();
        Pedido p2 = pedidos.findByCodigoAtendimento(segundo).orElseThrow();
        assertThat(p1.isPossivelDuplicidade()).isFalse();
        assertThat(p2.isPossivelDuplicidade()).isTrue();
        assertThat(p2.getMotivoDuplicidade()).isNotBlank();
    }

    @Test
    void mesmoCpfEmPedidoAbertoTambemGeraAlerta() throws Exception {
        criarPedido(pedidoJson("Pessoa A", unico("Boa Viagem"), ",\"cpf\":\"" + CPF_DIGITOS + "\""), null);
        String segundo = criarPedido(pedidoJson("Pessoa B", unico("Pina"), ",\"cpf\":\"" + CPF_COM_PONTUACAO + "\""), null);

        Pedido p2 = pedidos.findByCodigoAtendimento(segundo).orElseThrow();
        assertThat(p2.isPossivelDuplicidade()).isTrue();
        assertThat(p2.getMotivoDuplicidade()).contains("CPF");
    }

    // ----------------------------------------------------- atendimento assistido

    @Test
    void pedidoFeitoPorAssistenteLogadoFicaMarcadoComoAssistido() throws Exception {
        String codigo = criarPedido(pedidoJson("Seu Raimundo", unico("Centro"), ""), basic(CRAS));

        Pedido p = pedidos.findByCodigoAtendimento(codigo).orElseThrow();
        assertThat(p.getOrigem()).isEqualTo(Origem.ASSISTIDO);
        assertThat(p.getPreenchidoPor()).isEqualTo("Ana do CRAS");
    }

    @Test
    void assistidoSemLoginUsaOServicoInformadoNoFormulario() throws Exception {
        String codigo = criarPedido(pedidoJson("Dona Lúcia", unico("Centro"),
                ",\"preenchidoPor\":\"Centro POP Recife\""), null);

        Pedido p = pedidos.findByCodigoAtendimento(codigo).orElseThrow();
        assertThat(p.getOrigem()).isEqualTo(Origem.ASSISTIDO);
        assertThat(p.getPreenchidoPor()).isEqualTo("Centro POP Recife");
    }

    @Test
    void pedidoSemAssistenciaFicaComoSozinho() throws Exception {
        String codigo = criarPedido(pedidoJson("Carlos", unico("Centro"), ""), null);
        assertThat(pedidos.findByCodigoAtendimento(codigo).orElseThrow().getOrigem()).isEqualTo(Origem.SOZINHO);
    }

    @Test
    void credenciaisInvalidasNoPostSaoRecusadasEPerfilEstoqueNaoPreenche() throws Exception {
        String corpo = pedidoJson("Fulano", unico("Centro"), "");
        mvc.perform(post("/pedidos").contentType(MediaType.APPLICATION_JSON).content(corpo)
                        .header("Authorization", basic(CRAS, "senha-errada-123")))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/pedidos").contentType(MediaType.APPLICATION_JSON).content(corpo)
                        .header("Authorization", basic(ESTOQUE)))
                .andExpect(status().isForbidden());
    }

    // ------------------------------------------------------ acesso da equipe

    @Test
    void areaDaEquipeExigeLoginEPerfil() throws Exception {
        mvc.perform(get("/equipe/pedidos"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().exists("WWW-Authenticate"));
        mvc.perform(get("/equipe/pedidos").header("Authorization", basic(TRIAGEM, "senha-errada-123")))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/equipe/pedidos").header("Authorization", basic("ninguem@exemplo.org", SENHA)))
                .andExpect(status().isUnauthorized());

        // quem opera estoque não vê dados pessoais; assistente do CRAS só preenche
        mvc.perform(get("/equipe/pedidos").header("Authorization", basic(ESTOQUE)))
                .andExpect(status().isForbidden());
        mvc.perform(get("/equipe/pedidos").header("Authorization", basic(CRAS)))
                .andExpect(status().isForbidden());

        mvc.perform(get("/equipe/pedidos").header("Authorization", basic(TRIAGEM)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conteudo").isArray());
    }

    @Test
    void listaPedidosFiltrandoPorStatus() throws Exception {
        criarPedido(pedidoJson("Listado", unico("Torre"), ""), null);

        mvc.perform(get("/equipe/pedidos").param("status", "RECEBIDO").header("Authorization", basic(TRIAGEM)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").isNumber())
                .andExpect(jsonPath("$.conteudo[0].status").value("RECEBIDO"));

        mvc.perform(get("/equipe/pedidos").param("status", "INEXISTENTE").header("Authorization", basic(TRIAGEM)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void percorreOFluxoDeStatusEBloqueiaTransicoesInvalidas() throws Exception {
        String codigo = criarPedido(pedidoJson("Fluxo", unico("Várzea"), ""), null);
        Long id = pedidos.findByCodigoAtendimento(codigo).orElseThrow().getId();
        String triagem = basic(TRIAGEM);

        atualizar(id, "{\"status\":\"RETIRADO\"}", triagem).andExpect(status().isConflict());
        atualizar(id, "{}", triagem).andExpect(status().isBadRequest());
        atualizar(id, "{\"status\":\"EM_ANALISE\"}", triagem)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_ANALISE"));

        // não pode aguardar retirada sem dizer onde retirar
        atualizar(id, "{\"status\":\"AGUARDANDO_RETIRADA\"}", triagem).andExpect(status().isBadRequest());

        atualizar(id, "{\"status\":\"AGUARDANDO_RETIRADA\",\"pontoRetirada\":\"CRAS Casa Amarela\"}", triagem)
                .andExpect(status().isOk());

        // a pessoa vê o status e onde retirar, e só isso
        mvc.perform(get("/pedidos/" + codigo))
                .andExpect(jsonPath("$.status").value("AGUARDANDO_RETIRADA"))
                .andExpect(jsonPath("$.pontoRetirada").value("CRAS Casa Amarela"));

        atualizar(id, "{\"status\":\"RETIRADO\"}", triagem).andExpect(status().isOk());
        atualizar(id, "{\"status\":\"CANCELADO\"}", triagem).andExpect(status().isConflict());
        mvc.perform(get("/pedidos/" + codigo)).andExpect(jsonPath("$.pontoRetirada").doesNotExist());
    }

    @Test
    void somenteAdminConfirmaOCadunico() throws Exception {
        String codigo = criarPedido(pedidoJson("Cad", unico("Torre"), ",\"cadunico\":\"SIM\""), null);
        Long id = pedidos.findByCodigoAtendimento(codigo).orElseThrow().getId();

        mvc.perform(post("/equipe/pedidos/" + id + "/cadunico/verificar").header("Authorization", basic(TRIAGEM)))
                .andExpect(status().isForbidden());
        mvc.perform(post("/equipe/pedidos/" + id + "/cadunico/verificar").header("Authorization", basic(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.solicitante.statusCadunico").value("VERIFICADO"));
    }

    // ---------------------------------------------------------- usuários

    @Test
    void adminCriaEDesativaUsuariosEOutrosPerfisNao() throws Exception {
        String email = "novo-" + UUID.randomUUID().toString().substring(0, 8) + "@exemplo.org";
        String corpo = """
                {"nome":"Novo Triador","email":"%s","senha":"%s","perfil":"TRIAGEM"}
                """.formatted(email, SENHA);

        mvc.perform(post("/equipe/usuarios").contentType(MediaType.APPLICATION_JSON).content(corpo)
                        .header("Authorization", basic(TRIAGEM)))
                .andExpect(status().isForbidden());

        String resposta = mvc.perform(post("/equipe/usuarios").contentType(MediaType.APPLICATION_JSON).content(corpo)
                        .header("Authorization", basic(ADMIN)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.senha").doesNotExist())
                .andExpect(jsonPath("$.senhaHash").doesNotExist())
                .andReturn().getResponse().getContentAsString();
        Number novoId = JsonPath.read(resposta, "$.id");

        mvc.perform(post("/equipe/usuarios").contentType(MediaType.APPLICATION_JSON).content(corpo)
                        .header("Authorization", basic(ADMIN)))
                .andExpect(status().isConflict());

        mvc.perform(get("/equipe/pedidos").header("Authorization", basic(email))).andExpect(status().isOk());

        mvc.perform(patch("/equipe/usuarios/" + novoId + "/ativo").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ativo\":false}").header("Authorization", basic(ADMIN)))
                .andExpect(status().isOk());
        mvc.perform(get("/equipe/pedidos").header("Authorization", basic(email))).andExpect(status().isUnauthorized());
    }

    // ---------------------------------------------------------- auditoria

    @Test
    void registraNaAuditoriaQuemFezOQueEVisualizouDadosPessoais() throws Exception {
        String codigo = criarPedido(pedidoJson("Auditado", unico("Torre"), ""), null);
        Long pedidoId = pedidos.findByCodigoAtendimento(codigo).orElseThrow().getId();
        Long triagemId = usuarios.findByEmailIgnoreCase(TRIAGEM).orElseThrow().getId();

        mvc.perform(get("/equipe/pedidos/" + pedidoId).header("Authorization", basic(TRIAGEM)))
                .andExpect(status().isOk());

        assertThat(auditorias.findAll()).anyMatch(a ->
                "CRIAR_PEDIDO".equals(a.getAcao()) && a.getUsuarioId() == null && Objects.equals(a.getRegistroId(), pedidoId));
        assertThat(auditorias.findAll()).anyMatch(a ->
                "VISUALIZAR_PEDIDO".equals(a.getAcao()) && Objects.equals(a.getUsuarioId(), triagemId)
                        && Objects.equals(a.getRegistroId(), pedidoId) && "pedido".equals(a.getTabelaAfetada()));

        mvc.perform(get("/equipe/auditoria").header("Authorization", basic(TRIAGEM))).andExpect(status().isForbidden());
        mvc.perform(get("/equipe/auditoria").header("Authorization", basic(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conteudo[0].acao").exists());
    }

    // ------------------------------------------------------------- ajudantes

    private String criarPedido(String corpo, String authorization) throws Exception {
        var requisicao = post("/pedidos").contentType(MediaType.APPLICATION_JSON).content(corpo);
        if (authorization != null) {
            requisicao = requisicao.header("Authorization", authorization);
        }
        String resposta = mvc.perform(requisicao)
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(resposta, "$.codigo");
    }

    private org.springframework.test.web.servlet.ResultActions atualizar(Long id, String corpo, String authorization)
            throws Exception {
        return mvc.perform(patch("/equipe/pedidos/" + id).contentType(MediaType.APPLICATION_JSON)
                .content(corpo).header("Authorization", authorization));
    }

    private void garantirUsuario(String email, String nome, Perfil perfil) {
        if (usuarios.findByEmailIgnoreCase(email).isEmpty()) {
            usuarios.save(new UsuarioEquipe(nome, email, encoder.encode(SENHA), perfil));
        }
    }

    private static String pedidoJson(String nome, String bairro, String extras) {
        return """
                {"nomeOuApelido":"%s","bairroOuReferencia":"%s","numPessoas":3,"consentimentoLgpd":true%s}
                """.formatted(nome, bairro, extras);
    }

    private static String unico(String base) {
        return base + " " + UUID.randomUUID().toString().substring(0, 8);
    }

    private static String basic(String email) {
        return basic(email, SENHA);
    }

    private static String basic(String email, String senha) {
        return "Basic " + Base64.getEncoder().encodeToString((email + ":" + senha).getBytes(StandardCharsets.UTF_8));
    }
}
