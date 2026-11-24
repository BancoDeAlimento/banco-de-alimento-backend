package br.recife.bancodealimentos.web;

import br.recife.bancodealimentos.dominio.Entidade;
import br.recife.bancodealimentos.repositorio.EntidadeRepository;
import br.recife.bancodealimentos.seguranca.CriptografiaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class EntidadesApiTest {

    @Autowired private WebApplicationContext contexto;
    @Autowired private EntidadeRepository entidades;
    @Autowired private CriptografiaService criptografia;
    private MockMvc mvc;

    @BeforeEach
    void preparar() {
        mvc = MockMvcBuilders.webAppContextSetup(contexto).build();
    }

    private static String json(String documento) {
        return """
                {"nome":"Cozinha Solidária Esperança","tipo":"COZINHA_SOLIDARIA",
                 "documento":"%s","contato":"81 99999-0000","bairro":"Ibura",
                 "consentimentoLgpd":true}
                """.formatted(documento);
    }

    @Test
    void criaEntidadeComCnpjValidoSemExporDocumento() throws Exception {
        mvc.perform(post("/entidades").contentType(MediaType.APPLICATION_JSON).content(json("11.222.333/0001-81")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.documento").doesNotExist())
                .andExpect(jsonPath("$.contato").doesNotExist());
    }

    @Test
    void recusaDocumentoComDigitoErrado() throws Exception {
        mvc.perform(post("/entidades").contentType(MediaType.APPLICATION_JSON).content(json("11.222.333/0001-82")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void aceitaCnpjAlfanumerico() throws Exception {
        mvc.perform(post("/entidades").contentType(MediaType.APPLICATION_JSON).content(json("00.000.000/E08G-12")))
                .andExpect(status().isCreated());
    }

    @Test
    void duplicidadeNaoRecusaApenasSinaliza() throws Exception {
        String corpo = json("12.ABC.345/01DE-35");
        mvc.perform(post("/entidades").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isCreated());
        mvc.perform(post("/entidades").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isCreated());

        String indice = criptografia.indiceCego("12ABC34501DE35");
        List<Entidade> mesmas = entidades.findAll().stream()
                .filter(e -> indice.equals(e.getDocumentoIndice()))
                .toList();
        assertThat(mesmas).hasSizeGreaterThanOrEqualTo(2);
        assertThat(mesmas).anyMatch(Entidade::isPossivelDuplicidade);
    }

    @Test
    void exigeCamposObrigatoriosEConsentimento() throws Exception {
        mvc.perform(post("/entidades").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"consentimentoLgpd\":false}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.nome").exists())
                .andExpect(jsonPath("$.erros.consentimentoLgpd").exists());
    }
}